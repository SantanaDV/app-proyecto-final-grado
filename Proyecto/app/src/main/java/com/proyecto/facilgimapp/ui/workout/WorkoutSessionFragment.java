package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutSessionBinding;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;
import com.proyecto.facilgimapp.viewmodel.WorkoutSessionViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSessionFragment extends Fragment {
    private FragmentWorkoutSessionBinding b;
    private ExercisesViewModel exercisesVM;
    private Chronometer chronometer;
    private WorkoutSessionAdapter adapter;
    private WorkoutSessionViewModel viewModel;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup ctr,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutSessionBinding.inflate(inf, ctr, false);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        chronometer = b.chronometer;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        EntrenamientoDTO workoutDTO = WorkoutSessionFragmentArgs.fromBundle(getArguments()).getNewWorkoutDTO();
        int[] exerciseIds = WorkoutSessionFragmentArgs.fromBundle(getArguments()).getExerciseIds();
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), allExercises -> {
            if (allExercises == null || allExercises.isEmpty()) {
                Toast.makeText(requireContext(), "No se pudieron cargar los ejercicios", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack();
                return;
            }

            List<EjercicioDTO> selectedExercises = new ArrayList<>();
            for (int id : exerciseIds) {
                for (EjercicioDTO ejercicio : allExercises) {
                    if (ejercicio.getIdEjercicio() == id) {
                        selectedExercises.add(ejercicio);
                        break;
                    }
                }
            }

            adapter = new WorkoutSessionAdapter(workoutDTO, selectedExercises);
            b.rvSessionExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
            b.rvSessionExercises.setAdapter(adapter);
        });
        viewModel = new ViewModelProvider(this).get(WorkoutSessionViewModel.class);

        b.btnFinishSession.setOnClickListener(btn -> {
            if (!adapter.allCompleted()) return;

            long elapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
            workoutDTO.setDuracion((int) elapsed);

            viewModel.saveWorkoutSession(workoutDTO, adapter.getSeriesMap());

            Navigation.findNavController(btn).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
