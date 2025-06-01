package com.proyecto.facilgimapp.ui.workout;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentNewWorkoutBinding;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.ui.exercises.ExerciseSelectionAdapter;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;
import com.proyecto.facilgimapp.viewmodel.NewWorkoutViewModel;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment encargado de crear un nuevo entrenamiento paso a paso.
 * <p>
 * Muestra campos para nombre, descripción, fecha, tipo de entrenamiento y
 * una lista de ejercicios seleccionables. Guarda los datos parciales en un ViewModel
 * (borrador), valida entradas y navega al siguiente paso (WorkoutSessionFragment)
 * con los datos compilados.
 * </p>
 *
 * @author Francisco Santana
 */
public class NewWorkoutFragment extends Fragment {

    /**
     * Binding para acceder a las vistas del layout fragment_new_workout.xml.
     */
    private FragmentNewWorkoutBinding b;

    /**
     * ViewModel que proporciona la lista de tipos de entrenamiento.
     */
    private TypeViewModel typeVM;

    /**
     * ViewModel que proporciona la lista de ejercicios disponibles.
     */
    private ExercisesViewModel exercisesVM;

    /**
     * ViewModel que guarda en borrador los datos ingresados para el nuevo entrenamiento.
     */
    private NewWorkoutViewModel draftVM;

    /**
     * Adaptador para mostrar y seleccionar ejercicios disponibles.
     */
    private ExerciseSelectionAdapter exerciseAdapter;

    /**
     * Fecha seleccionada para el entrenamiento.
     */
    private LocalDate selectedDate;

    /**
     * Infla el layout del fragment, inicializa los ViewModels y devuelve la vista raíz.
     *
     * @param inflater           Inflador de vistas de Android.
     * @param container          Contenedor padre donde se insertará el fragmento.
     * @param savedInstanceState Bundle con el estado previo del fragmento; puede ser null.
     * @return Vista raíz inflada correspondiente a fragment_new_workout.xml.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentNewWorkoutBinding.inflate(inflater, container, false);

        // Instancia de los ViewModels
        typeVM     = new ViewModelProvider(this).get(TypeViewModel.class);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        draftVM     = new ViewModelProvider(this).get(NewWorkoutViewModel.class);

        return b.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>Spinner de tipos de entrenamiento (y botón para agregar nuevos si es admin).</li>
     *     <li>DatePicker para seleccionar la fecha del entrenamiento.</li>
     *     <li>RecyclerView con ExerciseSelectionAdapter para elegir ejercicios.</li>
     *     <li>Botón "Siguiente" que valida entradas, guarda datos en el draftVM y navega
     *         a WorkoutSessionFragment con los datos recopilados.</li>
     *     <li>Reposición de valores guardados en el draftVM (fecha, tipo, ejercicios, nombre).</li>
     * </ul>
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado previo; puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Configuración del Spinner de tipos de entrenamiento
        if (!SessionManager.isAdmin(requireContext())) {
            b.btnAddType.setVisibility(View.GONE);
        } else {
            b.btnAddType.setOnClickListener(v ->
                    Navigation.findNavController(v)
                            .navigate(R.id.action_newWorkoutFragment_to_typeListFragment)
            );
        }
        typeVM.getTypes().observe(getViewLifecycleOwner(), types -> {
            if (types == null) return;

            List<String> nombres = new ArrayList<>();
            for (TipoEntrenamientoDTO t : types) {
                nombres.add(t.getNombre());
            }

            ArrayAdapter<String> adapter;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        nombres
                );
            } else {
                adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        nombres
                );
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            b.spinnerType.setAdapter(adapter);

            // Si ya había una posición guardada en draftVM, selecciónala en el Spinner
            Integer savedPos = draftVM.draftTypePosition().getValue();
            if (savedPos != null && savedPos < nombres.size()) {
                b.spinnerType.setSelection(savedPos);
            }
        });
        typeVM.loadTypes();

        // Configuración del DatePicker para la fecha de entrenamiento
        b.btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (DatePicker dp, int y, int m, int d) -> {
                        selectedDate = LocalDate.of(y, m + 1, d);
                        b.btnPickDate.setText(selectedDate.toString());
                        draftVM.setDraftDate(selectedDate); // Guarda en el ViewModel
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Si ya había una fecha guardada en draftVM, muéstrala
        LocalDate savedDate = draftVM.draftDate().getValue();
        if (savedDate != null) {
            selectedDate = savedDate;
            b.btnPickDate.setText(savedDate.toString());
        }

        // Configuración del RecyclerView de ejercicios disponibles
        exerciseAdapter = new ExerciseSelectionAdapter();
        b.rvAvailableExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvAvailableExercises.setAdapter(exerciseAdapter);

        // Carga del catálogo de ejercicios
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                exerciseAdapter.setExercises(list);

                // Si ya había ejercicios seleccionados en draftVM, repónelos
                List<Integer> savedIds = draftVM.draftExercises().getValue();
                if (savedIds != null && !savedIds.isEmpty()) {
                    exerciseAdapter.setInitiallySelectedIds(savedIds);
                }
            } else {
                Log.d("DEBUG", "No hay ejercicios disponibles.");
            }
        });

        // Botón "Siguiente": valida campos y navega a WorkoutSessionFragment
        b.btnNextToExercises.setOnClickListener(v -> {
            boolean valid = true;

            // Validar nombre no vacío
            String nombre = b.etName.getText().toString().trim();
            if (nombre.isEmpty()) {
                b.etName.setError(getString(R.string.error_name_required));
                valid = false;
            }

            // Validar fecha seleccionada
            if (selectedDate == null) {
                Toast.makeText(requireContext(),
                        R.string.error_date_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            // Validar al menos un ejercicio seleccionado
            List<Integer> selectedIds = exerciseAdapter.getSelectedExerciseIds();
            if (selectedIds == null || selectedIds.isEmpty()) {
                Toast.makeText(requireContext(),
                        R.string.error_exercises_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            // Validar selección de tipo de entrenamiento
            if (b.spinnerType.getSelectedItemPosition() < 0) {
                Toast.makeText(requireContext(),
                        R.string.error_type_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (!valid) return;

            // Guardar valores en draftVM antes de navegar
            int pos = b.spinnerType.getSelectedItemPosition();
            draftVM.setDraftTypePosition(pos);
            draftVM.setDraftExercises(selectedIds);

            // Construcción del DTO para el nuevo entrenamiento
            EntrenamientoDTO dto = new EntrenamientoDTO();
            dto.setNombre(nombre);
            dto.setDescripcion(b.etDescription.getText().toString().trim());
            dto.setFechaEntrenamiento(selectedDate);
            int tipoId = typeVM.getTypes().getValue()
                    .get(pos)
                    .getId();
            dto.setTipoEntrenamiento(new TipoEntrenamientoDTO(tipoId));
            UsuarioDTO user = new UsuarioDTO();
            user.setIdUsuario(SessionManager.getUserId(requireContext()));
            dto.setUsuario(user);
            dto.setEjerciciosId(selectedIds);
            draftVM.setDraftWorkout(dto);

            // Navegar con dirección segura y argumentos al WorkoutSessionFragment
            int[] exerciseIdsArray = selectedIds.stream().mapToInt(Integer::intValue).toArray();
            Navigation.findNavController(v).navigate(
                    R.id.action_newWorkoutFragment_to_workoutSessionFragment,
                    NewWorkoutFragmentDirections
                            .actionNewWorkoutFragmentToWorkoutSessionFragment(dto, exerciseIdsArray)
                            .getArguments()
            );
        });

        // Si ya había datos guardados en draftVM, repón nombre y descripción
        EntrenamientoDTO savedDto = draftVM.draftWorkout().getValue();
        if (savedDto != null && savedDto.getNombre() != null) {
            b.etName.setText(savedDto.getNombre());
            b.etDescription.setText(savedDto.getDescripcion());
        }
    }

    /**
     * Se llama cuando la vista del fragmento se destruye. Libera la referencia al binding
     * para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
