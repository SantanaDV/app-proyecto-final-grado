package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutDetailBinding;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;
import com.proyecto.facilgimapp.viewmodel.WorkoutDetailViewModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkoutDetailFragment extends Fragment {
    private FragmentWorkoutDetailBinding b;
    private WorkoutDetailViewModel vm;
    private RelationAdapter adapter;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutDetailBinding.inflate(inflater, container, false);

        // ViewModels
        vm     = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        // RecyclerView de relaciones
        b.rvRelations.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider =
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_light)
        );
        b.rvRelations.addItemDecoration(divider);



        // Observamos el workout
        vm.getWorkout().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;

            //  Header: nombre
            b.tvWorkoutName.setText(dto.getNombre());

            // Fecha
            String fechaStr = dto.getFechaEntrenamiento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            b.tvWorkoutDate.setText(getString(R.string.fecha_entrenamiento) + " " + fechaStr);


            //  Duración
            b.tvWorkoutDuration.setText(
                    getString(R.string.duraci_n_min, dto.getDuracion())
            );
            b.tvTypeName.setText(getString(R.string.tipo_entrenamiento)  + ": " + dto.getTipoEntrenamiento().getNombre());


            // 2e) Adapter de relaciones (series/ejercicios)
            if (adapter == null) {
                adapter = new RelationAdapter(
                        dto.convertirLFechaAString(),
                        dto.getDuracion()
                );
                b.rvRelations.setAdapter(adapter);

                vm.getRelations().observe(getViewLifecycleOwner(), list ->
                        adapter.submitList(list)
                );
            }
        });

        // Disparamos las cargas iniciales
        int workoutId = getArguments() != null
                ? getArguments().getInt("workoutId", 0)
                : 0;
        vm.loadDetails(workoutId);
        vm.loadRelations(workoutId);

        return b.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}