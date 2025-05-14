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
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class NewWorkoutFragment extends Fragment {

    private FragmentNewWorkoutBinding b;
    private TypeViewModel typeVM;
    private ExercisesViewModel exercisesVM;
    private ExerciseSelectionAdapter exerciseAdapter;
    private LocalDate selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentNewWorkoutBinding.inflate(inflater, container, false);
        typeVM = new ViewModelProvider(this).get(TypeViewModel.class);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Spinner de tipos
        if (!SessionManager.isAdmin(requireContext())) {
            b.btnAddType.setVisibility(View.GONE);
        }else {
            b.btnAddType.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_newWorkoutFragment_to_typeListFragment);
            });
        }
        typeVM.getTypes().observe(getViewLifecycleOwner(), types -> {
            ArrayAdapter<String> adapter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        types.stream().map(t -> t.getNombre()).toList()
                );
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            b.spinnerType.setAdapter(adapter);
        });
        typeVM.loadTypes();

        // Selector de fecha
        b.btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (DatePicker dp, int y, int m, int d) -> {
                        selectedDate = LocalDate.of(y, m + 1, d);
                        b.btnPickDate.setText(selectedDate.toString());
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // RecyclerView para selección de ejercicios
        exerciseAdapter = new ExerciseSelectionAdapter();
        b.rvAvailableExercises.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        b.rvAvailableExercises.setAdapter(exerciseAdapter);

        // Cargar catálogo completo
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                exerciseAdapter.setExercises(list);  // Aquí se actualiza el RecyclerView
            } else {
                Log.d("DEBUG", "No hay ejercicios disponibles.");
            }
        });

        // Botón “Siguiente”
        b.btnNextToExercises.setOnClickListener(v -> {
            EntrenamientoDTO dto = new EntrenamientoDTO();
            dto.setNombre(b.etName.getText().toString());
            dto.setDescripcion(b.etDescription.getText().toString());
            dto.setFechaEntrenamiento(selectedDate);
            int tipoId = typeVM.getTypes().getValue()
                    .get(b.spinnerType.getSelectedItemPosition())
                    .getId();

            dto.setTipoEntrenamiento(new TipoEntrenamientoDTO(tipoId));
            dto.setUsuario(new UsuarioDTO());
            dto.getUsuario().setIdUsuario(SessionManager.getUserId(requireContext()));
            dto.setEjerciciosId(exerciseAdapter.getSelectedExerciseIds());

            int[] exerciseIdsArray = exerciseAdapter
                    .getSelectedExerciseIds()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .toArray();

            Navigation.findNavController(v).navigate(
                    R.id.action_newWorkoutFragment_to_workoutSessionFragment,
                    NewWorkoutFragmentDirections
                            .actionNewWorkoutFragmentToWorkoutSessionFragment(dto, exerciseIdsArray)
                            .getArguments()
            );

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
