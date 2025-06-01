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
import com.proyecto.facilgimapp.viewmodel.WorkoutDetailViewModel;

import java.time.format.DateTimeFormatter;

/**
 * Fragment que muestra los detalles de un entrenamiento, incluyendo
 * nombre, fecha, duración, tipo y la lista de ejercicios con sus series.
 * <p>
 * Obtiene los datos mediante {@link WorkoutDetailViewModel} y utiliza
 * {@link RelationAdapter} para listar las relaciones ejercicio–serie.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class WorkoutDetailFragment extends Fragment {
    /**
     * Binding generado para acceder a las vistas definidas en fragment_workout_detail.xml.
     */
    private FragmentWorkoutDetailBinding b;

    /**
     * ViewModel que maneja la obtención de datos del entrenamiento y sus relaciones.
     */
    private WorkoutDetailViewModel vm;

    /**
     * Adaptador para mostrar las relaciones ejercicio–serie dentro del RecyclerView.
     */
    private RelationAdapter adapter;

    /**
     * Infla el layout del fragment, inicializa el ViewModel y configura el RecyclerView.
     * <p>
     * Observa los cambios en el entrenamiento y sus relaciones para actualizar la UI:
     * nombre, fecha formateada, duración, tipo, y lista de relaciones con {@link RelationAdapter}.
     * </p>
     *
     * @param inflater           Inflador de vistas proporcionado por Android.
     * @param container          Contenedor padre en el que se insertará el fragmento.
     * @param savedInstanceState Bundle con el estado anterior del fragmento; puede ser null.
     * @return Vista raíz inflada correspondiente a fragment_workout_detail.xml.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutDetailBinding.inflate(inflater, container, false);

        // Inicializa el ViewModel
        vm = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        // Configura RecyclerView para mostrar relaciones ejercicio–serie
        b.rvRelations.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider =
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_light)
        );
        b.rvRelations.addItemDecoration(divider);

        // Observa el LiveData del entrenamiento para actualizar UI
        vm.getWorkout().observe(getViewLifecycleOwner(), dto -> {
            if (dto == null) return;

            // Actualiza el nombre del entrenamiento
            b.tvWorkoutName.setText(dto.getNombre());

            // Formatea y muestra la fecha en "dd/MM/yyyy"
            String fechaStr = dto.getFechaEntrenamiento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            b.tvWorkoutDate.setText(getString(R.string.fecha_entrenamiento) + " " + fechaStr);

            // Muestra duración en minutos
            b.tvWorkoutDuration.setText(
                    getString(R.string.duraci_n_min, dto.getDuracion())
            );

            // Muestra el nombre del tipo de entrenamiento
            b.tvTypeName.setText(
                    getString(R.string.tipo_entrenamiento) + ": " +
                    dto.getTipoEntrenamiento().getNombre()
            );

            // Configura el adaptador de relaciones si aún no existe
            if (adapter == null) {
                adapter = new RelationAdapter(
                        dto.convertirLFechaAString(),
                        dto.getDuracion()
                );
                b.rvRelations.setAdapter(adapter);

                // Observa las relaciones ejercicio–serie y actualiza la lista
                vm.getRelations().observe(getViewLifecycleOwner(), list ->
                        adapter.submitList(list)
                );
            }
        });

        // Carga inicial de datos del entrenamiento y sus relaciones
        int workoutId = getArguments() != null
                ? getArguments().getInt("workoutId", 0)
                : 0;
        vm.loadDetails(workoutId);
        vm.loadRelations(workoutId);

        return b.getRoot();
    }

    /**
     * Libera la referencia al binding cuando la vista se destruye
     * para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
