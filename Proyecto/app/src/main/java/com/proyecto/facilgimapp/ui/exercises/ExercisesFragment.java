package com.proyecto.facilgimapp.ui.exercises;

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

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentExercisesBinding;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;

import java.util.List;

public class ExercisesFragment extends Fragment {
    private FragmentExercisesBinding binding;
    private ExercisesViewModel viewModel;
    private EjercicioDTOAdapter adapter;
    private int workoutId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            workoutId = getArguments().getInt("workoutId", -1);
        }

        viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);

        adapter = new EjercicioDTOAdapter(ejercicioId -> {
            Bundle args = new Bundle();
            args.putInt("workoutId", workoutId);
            args.putInt("exerciseId", ejercicioId);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_exercisesFragment_to_seriesFragment, args);
        });

        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);

        // Observamos la lista de ejercicios
        viewModel.getExercises().observe(getViewLifecycleOwner(), this::showExercises);
        // Cargamos los ejercicios para el workout y usuario actual
        String username = SessionManager.getUsername(requireContext());
        viewModel.loadExercises(workoutId, username);
    }

    private void showExercises(List<EjercicioDTO> list) {
        adapter.submitList(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
