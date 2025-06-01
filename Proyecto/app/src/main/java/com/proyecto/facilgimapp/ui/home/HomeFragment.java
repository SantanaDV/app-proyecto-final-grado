package com.proyecto.facilgimapp.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentHomeBinding;
import com.proyecto.facilgimapp.ui.exercises.EjercicioAdapter;
import com.proyecto.facilgimapp.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel vm;
    private EjercicioAdapter latestAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup ctr, Bundle b) {
        binding = FragmentHomeBinding.inflate(inf, ctr, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        vm = new ViewModelProvider(this).get(HomeViewModel.class);


        // Adapter últimos ejercicios
        latestAdapter = new EjercicioAdapter();
        binding.rvLatest.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvLatest.setAdapter(latestAdapter);

        // Observers
        vm.getTotalExercises().observe(getViewLifecycleOwner(),
                count -> binding.tvCountExercises.setText(String.valueOf(count)));
        vm.getTotalWorkouts().observe(getViewLifecycleOwner(),
                count -> binding.tvCountWorkouts.setText(String.valueOf(count)));
        vm.getTotalTypes().observe(getViewLifecycleOwner(),
                count -> binding.tvCountTypes.setText(String.valueOf(count)));
        vm.getLatestExercises().observe(getViewLifecycleOwner(),
                list  -> latestAdapter.submitList(list));

        // Calendario: decoradores
        vm.getWorkoutDates().observe(getViewLifecycleOwner(), dates -> {
            binding.calendarView.removeDecorators();
            binding.calendarView.addDecorator(
                    new WorkoutDecorator(requireContext(), dates)
            );
        });

        // Interceptar el botón atrás
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(R.string.salir_app)
                                .setMessage(R.string.seguro_salir_app)
                                .setPositiveButton(R.string.salir,
                                        (d, w) -> requireActivity().finish())
                                .setNegativeButton(R.string.action_cancel, null)
                                .show();
                    }
                }
        );

        vm.loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}