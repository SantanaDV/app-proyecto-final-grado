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
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.ui.exercises.EjercicioAdapter;
import com.proyecto.facilgimapp.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fragment que representa la pantalla principal (Home) de la aplicación.
 * <p>
 * Muestra estadísticas resumidas (total de ejercicios, entrenamientos y tipos),
 * presenta una vista horizontal con los últimos ejercicios agregados y un calendario
 * con decoradores para las fechas de entrenamientos. También intercepta el botón
 * atrás para confirmar la salida de la aplicación.
 * </p>
 *
 * @author Francisco Santana
 */
public class HomeFragment extends Fragment {
    /**
     * Binding generado para acceder a las vistas definidas en fragment_home.xml.
     */
    private FragmentHomeBinding binding;

    /**
     * ViewModel que obtiene datos de la capa de negocio para la pantalla Home,
     * como totales y lista de últimos ejercicios.
     */
    private HomeViewModel vm;

    /**
     * Adaptador para mostrar los ejercicios más recientes en un RecyclerView horizontal.
     */
    private EjercicioAdapter latestAdapter;

    /**
     * Infla el layout del fragment y devuelve la vista raíz.
     *
     * @param inf  Inflador de vistas proporcionado por Android.
     * @param ctr  Contenedor padre en el que se va a insertar el fragment.
     * @param b    Bundle con el estado anterior del fragment, puede ser null.
     * @return     Vista raíz inflada correspondiente a fragment_home.xml.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup ctr, Bundle b) {
        binding = FragmentHomeBinding.inflate(inf, ctr, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>Inicialización del ViewModel {@link HomeViewModel}.</li>
     *     <li>RecyclerView horizontal para mostrar los últimos ejercicios mediante {@link EjercicioAdapter}.</li>
     *     <li>Observadores sobre LiveData para actualizar contadores de ejercicios, entrenamientos y tipos.</li>
     *     <li>Observador para la lista de últimos ejercicios y carga en el adaptador.</li>
     *     <li>Decoradores en el CalendarView para marcar las fechas de entrenamientos.</li>
     *     <li>Interceptación del botón atrás para mostrar un diálogo de confirmación antes de salir.</li>
     *     <li>Llamada a {@link HomeViewModel#loadData()} para iniciar la carga de datos.</li>
     * </ul>
     *
     * @param v    Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param b    Bundle con el estado anterior del fragment, puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        vm = new ViewModelProvider(this).get(HomeViewModel.class);

        // Adapter para últimos ejercicios en vista horizontal
        latestAdapter = new EjercicioAdapter();
        binding.rvLatest.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvLatest.setAdapter(latestAdapter);

        // Observadores de LiveData para estadísticas
        vm.getTotalExercises().observe(getViewLifecycleOwner(),
                count -> binding.tvCountExercises.setText(String.valueOf(count)));
        vm.getTotalWorkouts().observe(getViewLifecycleOwner(),
                count -> binding.tvCountWorkouts.setText(String.valueOf(count)));
        vm.getTotalTypes().observe(getViewLifecycleOwner(),
                count -> binding.tvCountTypes.setText(String.valueOf(count)));

        // Observador para últimos ejercicios y carga en el adaptador
        vm.getLatestExercises().observe(getViewLifecycleOwner(), list -> {
            if (list == null) {
                latestAdapter.submitList(null);
                return;
            }
            //  Clonamos la lista recibida para no mutar la lista original
            List<EjercicioDTO> reversed = new ArrayList<>(list);
            // Invertimos la copia para mostrar los últimos primero
            Collections.reverse(reversed);
            // Enviamos el listado invertido al adapter
            latestAdapter.submitList(reversed);
        });

        // Calendario: aplica decoradores en fechas que tienen entrenamientos
        vm.getWorkoutDates().observe(getViewLifecycleOwner(), dates -> {
            binding.calendarView.removeDecorators();
            binding.calendarView.addDecorator(
                    new WorkoutDecorator(requireContext(), dates)
            );
        });

        // Interceptar el botón atrás para confirmar salida de la aplicación
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

        // Carga inicial de datos
        vm.loadData();
    }

    /**
     * Se llama cuando la vista del fragmento se destruye. Libera la referencia al binding
     * para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
