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
import androidx.navigation.NavOptions;
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

/**
 * Fragment que gestiona la ejecución de una sesión de entrenamiento.
 * <p>
 * Muestra un cronómetro para medir la duración real de la sesión,
 * carga los ejercicios seleccionados, y permite al usuario completar
 * series de cada ejercicio. Al finalizar, verifica que todas las series
 * estén completadas, calcula la duración en minutos, actualiza el DTO
 * de entrenamiento con las series y la duración, y guarda la sesión
 * mediante {@link WorkoutSessionViewModel}. Solo navega de vuelta cuando
 * la petición al servidor haya reportado éxito, para garantizar que
 * WorkoutsFragment recargue siempre la lista actualizada.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class WorkoutSessionFragment extends Fragment {
    private FragmentWorkoutSessionBinding b;
    private ExercisesViewModel exercisesVM;
    private Chronometer chronometer;
    private WorkoutSessionAdapter adapter;
    private WorkoutSessionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inf,
            ViewGroup ctr,
            Bundle savedInstanceState
    ) {
        b = FragmentWorkoutSessionBinding.inflate(inf, ctr, false);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        //  Arrancamos el cronómetro
        chronometer = b.chronometer;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        //  Recuperamos los  DTO y IDs desde los argumentos nav
        EntrenamientoDTO workoutDTO = WorkoutSessionFragmentArgs
                .fromBundle(getArguments())
                .getNewWorkoutDTO();
        int[] exerciseIds = WorkoutSessionFragmentArgs
                .fromBundle(getArguments())
                .getExerciseIds();

        //Cargamos todos los ejercicios y filtramo los seleccionados
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), allExercises -> {
            if (allExercises == null || allExercises.isEmpty()) {
                Toast.makeText(requireContext(),
                        R.string.no_pudieron_cargar_ejercicios,
                        Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack();
                return;
            }

            // Filtramos los ejercicios que coincidan con los IDs recibidos
            List<EjercicioDTO> selectedExercises = new ArrayList<>();
            for (int id : exerciseIds) {
                for (EjercicioDTO ejercicio : allExercises) {
                    if (ejercicio.getIdEjercicio() == id) {
                        selectedExercises.add(ejercicio);
                        break;
                    }
                }
            }

            //  Configuramos el Adapter
            adapter = new WorkoutSessionAdapter(workoutDTO, selectedExercises);
            b.rvSessionExercises.setLayoutManager(
                    new LinearLayoutManager(requireContext())
            );
            b.rvSessionExercises.setAdapter(adapter);
        });

        // Inicializamos el ViewModel para guardar
        viewModel = new ViewModelProvider(this).get(WorkoutSessionViewModel.class);

        // Manejamos el botón Finalizar sesión
        b.btnFinishSession.setOnClickListener(btn -> {
            // 6.a) Verificar que todas las series estén completadas
            if (!adapter.allCompleted()) {
                Toast.makeText(requireContext(),
                        R.string.debe_completar_todas_las_series,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculamos la duración en minutos
            long elapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
            int duracionMin = Math.round(elapsed / 60000f);
            if (duracionMin < 1) duracionMin = 1;

            //  Actualizamos el DTO
            workoutDTO.setDuracion(duracionMin);
            workoutDTO.setEntrenamientosEjercicios(
                    adapter.getEntrenamientoEjercicioDTOList()
            );

            //  Llamamos al ViewModel y pasarmos los callbacks
            viewModel.saveWorkoutSession(
                    workoutDTO,
                    adapter.getSeriesMap(),
                    // onSuccess:
                    () -> requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(),
                                R.string.guardado_correctamente,
                                Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(btn).navigate(
                                R.id.workoutsFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.workoutSessionFragment, true)
                                        .setPopUpTo(R.id.workoutsFragment, true)
                                        .build()
                        );
                    }),
                    // onError:
                    () -> requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(),
                                R.string.error_guardar_entrenamiento,
                                Toast.LENGTH_SHORT).show();
                    })
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
