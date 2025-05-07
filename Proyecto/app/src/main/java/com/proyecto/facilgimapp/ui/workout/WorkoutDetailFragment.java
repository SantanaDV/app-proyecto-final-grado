package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutDetailBinding;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.ui.exercises.EjercicioDTOAdapter;
import com.proyecto.facilgimapp.viewmodel.WorkoutDetailViewModel;

import java.util.List;

public class WorkoutDetailFragment extends Fragment {
    private FragmentWorkoutDetailBinding binding;
    private WorkoutDetailViewModel vm;
    private EjercicioDTOAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        int workoutId = getArguments() != null
                ? getArguments().getInt("workoutId") : 0;

        //  usa EjercicioDTOAdapter
        adapter = new EjercicioDTOAdapter(ejercicioId -> {
            Bundle args = new Bundle();
            args.putInt("workoutId", workoutId);
            args.putInt("exerciseId", ejercicioId);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_workoutDetailFragment_to_exercisesFragment, args);
        });

        binding.rvExercises.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvExercises.setAdapter(adapter);

        // 2) observa LiveData<List<EjercicioDTO>>
        vm.exercises.observe(getViewLifecycleOwner(), ejerciciosDTO -> {
            adapter.submitList(ejerciciosDTO);
        });

        vm.load(workoutId);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}