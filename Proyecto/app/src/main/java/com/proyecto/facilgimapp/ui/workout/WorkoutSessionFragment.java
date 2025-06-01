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
 * mediante {@link WorkoutSessionViewModel}.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class WorkoutSessionFragment extends Fragment {
    /**
     * Binding para acceder a las vistas del layout fragment_workout_session.xml.
     */
    private FragmentWorkoutSessionBinding b;

    /**
     * ViewModel que proporciona la lista de todos los ejercicios.
     */
    private ExercisesViewModel exercisesVM;

    /**
     * Cronómetro que mide el tiempo transcurrido desde el inicio de la sesión.
     */
    private Chronometer chronometer;

    /**
     * Adaptador que muestra cada ejercicio y sus series dentro de la sesión.
     */
    private WorkoutSessionAdapter adapter;

    /**
     * ViewModel que guarda la sesión de entrenamiento finalizada en el backend.
     */
    private WorkoutSessionViewModel viewModel;

    /**
     * Infla el layout del fragment, inicializa {@link ExercisesViewModel} y retorna la vista raíz.
     *
     * @param inf      Inflador de vistas proporcionado por Android.
     * @param ctr      Contenedor padre en el que se insertará el fragmento.
     * @param savedInstanceState Bundle con el estado previo del fragmento; puede ser null.
     * @return Vista raíz inflada correspondiente a fragment_workout_session.xml.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup ctr,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutSessionBinding.inflate(inf, ctr, false);
        exercisesVM = new ViewModelProvider(this).get(ExercisesViewModel.class);
        return b.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura el cronómetro,
     * carga los ejercicios seleccionados, inicializa el adaptador y el ViewModel
     * para guardar la sesión. También configura el botón de finalizar sesión.
     *
     * @param v Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param s Bundle con el estado previo del fragmento; puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        // Inicia y arranca el cronómetro desde cero
        chronometer = b.chronometer;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // Recupera el DTO de entrenamiento y los IDs de ejercicios seleccionados
        EntrenamientoDTO workoutDTO = WorkoutSessionFragmentArgs.fromBundle(getArguments())
                .getNewWorkoutDTO();
        int[] exerciseIds = WorkoutSessionFragmentArgs.fromBundle(getArguments())
                .getExerciseIds();

        // Solicita la lista completa de ejercicios
        exercisesVM.listAllExercises();
        exercisesVM.getAllExercises().observe(getViewLifecycleOwner(), allExercises -> {
            if (allExercises == null || allExercises.isEmpty()) {
                Toast.makeText(requireContext(),
                        R.string.no_pudieron_cargar_ejercicios,
                        Toast.LENGTH_SHORT).show();
                // Si no se pueden cargar ejercicios, retrocede en la navegación
                Navigation.findNavController(v).popBackStack();
                return;
            }

            // Filtra los ejercicios seleccionados por sus IDs
            List<EjercicioDTO> selectedExercises = new ArrayList<>();
            for (int id : exerciseIds) {
                for (EjercicioDTO ejercicio : allExercises) {
                    if (ejercicio.getIdEjercicio() == id) {
                        selectedExercises.add(ejercicio);
                        break;
                    }
                }
            }

            // Configura el adaptador con el DTO y los ejercicios seleccionados
            adapter = new WorkoutSessionAdapter(workoutDTO, selectedExercises);
            b.rvSessionExercises.setLayoutManager(
                    new LinearLayoutManager(requireContext())
            );
            b.rvSessionExercises.setAdapter(adapter);
        });

        // Inicializa el ViewModel para guardar la sesión
        viewModel = new ViewModelProvider(this).get(WorkoutSessionViewModel.class);

        // Configura el botón de finalizar sesión
        b.btnFinishSession.setOnClickListener(btn -> {
            // Verifica que todas las series estén completadas
            if (!adapter.allCompleted()) {
                Toast.makeText(requireContext(),
                        R.string.debe_completar_todas_las_series,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Calcula la duración en minutos a partir del cronómetro
            long elapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
            int duracionMin = Math.round(elapsed / 60000f); // convierte a minutos y redondea
            if (duracionMin < 1) duracionMin = 1; // mínimo 1 minuto

            // Actualiza el DTO con la duración y las relaciones ejercicio–serie
            workoutDTO.setDuracion(duracionMin);
            workoutDTO.setEntrenamientosEjercicios(
                    adapter.getEntrenamientoEjercicioDTOList()
            );
            // Guarda la sesión en el ViewModel
            viewModel.saveWorkoutSession(workoutDTO, adapter.getSeriesMap());

            // Navega de regreso a WorkoutsFragment, limpiando el back stack
            Navigation.findNavController(btn).navigate(
                    R.id.workoutsFragment,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.workoutSessionFragment, true)
                            .setPopUpTo(R.id.workoutsFragment, true)
                            .build()
            );
        });
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
