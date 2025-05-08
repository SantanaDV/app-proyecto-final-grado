package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutDetailBinding;
import com.proyecto.facilgimapp.viewmodel.WorkoutDetailViewModel;

public class WorkoutDetailFragment extends Fragment {
    private FragmentWorkoutDetailBinding b;
    private WorkoutDetailViewModel vm;
    private RelationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutDetailBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        int workoutId = getArguments() != null
                ? getArguments().getInt("workoutId") : 0;

        // Configura Recycler (sin adapter aún)
        b.rvRelations.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider =
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_light)
        );
        b.rvRelations.addItemDecoration(divider);

        // 1) Observa los detalles del workout para inicializar el header y el adapter
        vm.workout.observe(getViewLifecycleOwner(), entrenamiento -> {
            // Header
            b.tvWorkoutName.setText(entrenamiento.getNombre());
            b.tvWorkoutDate.setText("Fecha: " + entrenamiento.getFechaEntrenamiento());
            b.tvWorkoutDuration.setText("Duración: " + entrenamiento.getDuracionMinutos() + " min");

            // Ahora que tenemos fecha y duración, creamos el adapter
            if (adapter == null) {
                adapter = new RelationAdapter(
                        entrenamiento.getFechaEntrenamiento(),
                        entrenamiento.getDuracionMinutos()
                );
                b.rvRelations.setAdapter(adapter);

                // Observa relaciones
                vm.relations.observe(getViewLifecycleOwner(), list -> {
                    adapter.submitList(list);
                });
            }
        });

        // 2) Dispara las cargas
        vm.loadDetails(workoutId);
        vm.loadRelations(workoutId);

        // Back arrow
        b.toolbarWorkoutDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        return b.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
