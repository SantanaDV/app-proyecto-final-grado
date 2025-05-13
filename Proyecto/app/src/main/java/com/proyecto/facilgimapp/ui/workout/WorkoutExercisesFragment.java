package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutExercisesBinding;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.ui.exercises.ExerciseSelectionAdapter;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;
import com.proyecto.facilgimapp.util.SessionManager;
import java.util.List;

public class WorkoutExercisesFragment extends Fragment {

    private FragmentWorkoutExercisesBinding b;
    private ExerciseSelectionAdapter adapter;
    private ExercisesViewModel exercisesVM;
    private EntrenamientoDTO dto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutExercisesBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Recuperar DTO del nuevo entrenamiento
        dto = WorkoutExercisesFragmentArgs
                .fromBundle(getArguments())
                .getNewWorkoutDTO();

        // Configurar RecyclerView de selección
        adapter = new ExerciseSelectionAdapter();
        b.rvAvailableExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvAvailableExercises.setAdapter(adapter);

        // Cargar ejercicios según el tipo
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        exercisesVM.loadExercises(
                dto.getTipoEntrenamientoId(),
                SessionManager.getUsername(requireContext())
        );
        exercisesVM.getExercises().observe(getViewLifecycleOwner(), list -> {
            adapter.setExercises(list);
        });

        // Al confirmar, pasar DTO + array de IDs seleccionados
        b.btnConfirmExercises.setOnClickListener(v -> {
            List<Integer> selected = adapter.getSelectedExerciseIds();
            dto.setEjerciciosId(selected);
            // Convertir List<Integer> a int[]
            int[] idsArray = selected.stream().mapToInt(i -> i).toArray();
            Navigation.findNavController(v)
                    .navigate(
                            WorkoutExercisesFragmentDirections
                                    .actionWorkoutExercisesFragmentToWorkoutSessionFragment(
                                            dto,
                                            idsArray
                                    )
                    );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
