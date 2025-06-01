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

public class NewWorkoutFragment extends Fragment {

    private FragmentNewWorkoutBinding b;
    private TypeViewModel typeVM;
    private ExercisesViewModel exercisesVM;
    private NewWorkoutViewModel draftVM;
    private ExerciseSelectionAdapter exerciseAdapter;
    private LocalDate selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentNewWorkoutBinding.inflate(inflater, container, false);

        // Instancia de los ViewModels
        typeVM    = new ViewModelProvider(this).get(TypeViewModel.class);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        draftVM   = new ViewModelProvider(this).get(NewWorkoutViewModel.class);

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //  Spinner de tipos
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

            // Si ya había una posición elegida en el ViewModel, se repone el spinner aquí
            Integer savedPos = draftVM.draftTypePosition().getValue();
            if (savedPos != null && savedPos < nombres.size()) {
                b.spinnerType.setSelection(savedPos);
            }
        });
        typeVM.loadTypes();

        // Selector de fecha
        b.btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (DatePicker dp, int y, int m, int d) -> {
                        selectedDate = LocalDate.of(y, m + 1, d);
                        b.btnPickDate.setText(selectedDate.toString());
                        draftVM.setDraftDate(selectedDate); // guardo en el ViewModel
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Si venimos de atrás y ya había fecha elegida:
        LocalDate savedDate = draftVM.draftDate().getValue();
        if (savedDate != null) {
            selectedDate = savedDate;
            b.btnPickDate.setText(savedDate.toString());
        }

        //  RecyclerView de selección de ejercicios
        exerciseAdapter = new ExerciseSelectionAdapter();
        b.rvAvailableExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvAvailableExercises.setAdapter(exerciseAdapter);

        // Cargo catálogo completo
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                exerciseAdapter.setExercises(list);

                // Si venimos de atrás y ya había ejercicios seleccionados, se reponen
                List<Integer> savedIds = draftVM.draftExercises().getValue();
                if (savedIds != null && !savedIds.isEmpty()) {
                    exerciseAdapter.setInitiallySelectedIds(savedIds);
                }
            } else {
                Log.d("DEBUG", "No hay ejercicios disponibles.");
            }
        });

        //  Validaciones y “Siguiente”
        b.btnNextToExercises.setOnClickListener(v -> {
            boolean valid = true;

            //  Nombre no vacío
            String nombre = b.etName.getText().toString().trim();
            if (nombre.isEmpty()) {
                b.etName.setError(getString(R.string.error_name_required));
                valid = false;
            }

            //  Fecha seleccionada
            if (selectedDate == null) {
                Toast.makeText(requireContext(),
                        R.string.error_date_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            //  Al menos un ejercicio
            List<Integer> selectedIds = exerciseAdapter.getSelectedExerciseIds();
            if (selectedIds == null || selectedIds.isEmpty()) {
                Toast.makeText(requireContext(),
                        R.string.error_exercises_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            //  Tipo en spinner
            if (b.spinnerType.getSelectedItemPosition() < 0) {
                Toast.makeText(requireContext(),
                        R.string.error_type_required, Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (!valid) return;

            //  Antes de navegar, guardo todo en el ViewModel
            int pos = b.spinnerType.getSelectedItemPosition();
            draftVM.setDraftTypePosition(pos);

            draftVM.setDraftExercises(selectedIds);

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

            int[] exerciseIdsArray = selectedIds.stream().mapToInt(Integer::intValue).toArray();
            Navigation.findNavController(v).navigate(
                    R.id.action_newWorkoutFragment_to_workoutSessionFragment,
                    NewWorkoutFragmentDirections
                            .actionNewWorkoutFragmentToWorkoutSessionFragment(dto, exerciseIdsArray)
                            .getArguments()
            );
        });

        EntrenamientoDTO savedDto = draftVM.draftWorkout().getValue();
        if (savedDto != null && savedDto.getNombre() != null) {
            b.etName.setText(savedDto.getNombre());
            b.etDescription.setText(savedDto.getDescripcion());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
