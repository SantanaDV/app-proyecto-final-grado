package com.proyecto.facilgimapp.ui.workout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutsBinding;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;
import com.proyecto.facilgimapp.viewmodel.WorkoutViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que muestra la lista de entrenamientos del usuario, permite buscar,
 * crear, editar, ver detalles y eliminar entrenamientos.
 * <p>
 * Utiliza {@link WorkoutAdapter} para presentar cada elemento, y observa
 * los datos mediante {@link WorkoutViewModel} y {@link TypeViewModel}.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class WorkoutsFragment extends Fragment {
    private FragmentWorkoutsBinding b;
    private WorkoutAdapter adapter;
    private TypeViewModel typeVm;
    private WorkoutViewModel vm;

    /**
     * Infla el layout del fragment, inicializa los ViewModels, configura el RecyclerView
     * y observa los entrenamientos del usuario.
     *
     * @param inflater           Inflador de vistas proporcionado por Android
     * @param container          Contenedor padre en el que se insertará este fragment
     * @param savedInstanceState Bundle con el estado previo del fragmento; puede ser null
     * @return Vista raíz inflada correspondiente a fragment_workouts.xml
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutsBinding.inflate(inflater, container, false);

        // Inicializa el ViewModel de entrenamientos
        vm = new ViewModelProvider(this).get(WorkoutViewModel.class);

        // Inicializa el ViewModel de tipos y solicita carga
        typeVm = new ViewModelProvider(this).get(TypeViewModel.class);
        typeVm.loadTypes();  // dispara la carga de tipos

        // Configura el adaptador con callbacks para cada acción
        adapter = new WorkoutAdapter(
            new ArrayList<>(),
            workout -> {
                // Al hacer clic, navega a WorkoutDetailFragment con el ID
                Bundle args = new Bundle();
                args.putInt("workoutId", workout.getId());
                Navigation.findNavController(b.getRoot())
                          .navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, args);
            },
            workout -> mostrarDescripcion(workout),
            workout -> editarWorkout(workout),
            workout -> confirmarEliminacion(workout)
        );

        // Configura RecyclerView con LayoutManager vertical y el adaptador
        b.rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvWorkouts.setAdapter(adapter);

        // Observa la lista de entrenamientos y actualiza la UI
        vm.getWorkouts().observe(getViewLifecycleOwner(), list -> {
            adapter.updateList(list);
            boolean empty = list == null || list.isEmpty();
            b.tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.rvWorkouts.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        // Carga inicialmente los entrenamientos del usuario actual
        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);

        // Configura el menú de búsqueda en la barra de acciones
        configurarMenu();
        return b.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura el botón flotante
     * para crear un nuevo entrenamiento.
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}
     * @param savedInstanceState Bundle con el estado previo; puede ser null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón para crear un nuevo entrenamiento
        b.fabNewWorkout.setOnClickListener(v ->
            Navigation.findNavController(v)
                      .navigate(R.id.action_workoutsFragment_to_newWorkoutFragment)
        );
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

    /**
     * Configura el menú de búsqueda en la barra de acciones utilizando MenuProvider.
     * Añade un SearchView que filtra la lista de entrenamientos conforme se escribe.
     */
    private void configurarMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_search, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                androidx.appcompat.widget.SearchView searchView =
                        (androidx.appcompat.widget.SearchView) searchItem.getActionView();

                searchView.setQueryHint(getString(R.string.buscar_entrenamientos));
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) { return false; }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                return false; // No se manejan otros ítems
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * Muestra un diálogo con la descripción completa de un entrenamiento.
     *
     * @param workout DTO de {@link EntrenamientoDTO} del cual se mostrará la descripción
     */
    private void mostrarDescripcion(EntrenamientoDTO workout) {
       String descripcion = workout.getDescripcion().isEmpty() ? getString(R.string.sin_descripcion)
                                                                : workout.getDescripcion();
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.descripcion)
                .setMessage(descripcion)
                .setPositiveButton(R.string.close, null)
                .show();
    }

    /**
     * Muestra un diálogo de confirmación para eliminar el entrenamiento.
     * Si se confirma, llama a {@link WorkoutViewModel#deleteWorkout(int, Runnable, Runnable)}
     * y recarga la lista de entrenamientos.
     *
     * @param workout DTO de {@link EntrenamientoDTO} a eliminar
     */
    private void confirmarEliminacion(EntrenamientoDTO workout) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.eliminar)
                .setMessage(R.string.eliminar_entrenamiento)
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    vm.deleteWorkout(workout.getId(), () -> {
                        Toast.makeText(getContext(), R.string.eliminado, Toast.LENGTH_SHORT).show();
                        vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                    }, () -> Toast.makeText(getContext(), R.string.error_al_eliminar, Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /**
     * Inicia el flujo de edición de un entrenamiento existente.
     * <p>
     * Primero carga las relaciones ejercicio–serie mediante {@link WorkoutViewModel#loadWorkoutRelations},
     * luego obtiene la lista de tipos y muestra {@link EditWorkoutDialog} para editar el entrenamiento.
     * Al confirmar el diálogo, llama a {@link WorkoutViewModel#updateWorkout(int, EntrenamientoDTO, Runnable, Runnable)}.
     * </p>
     *
     * @param workout DTO de {@link EntrenamientoDTO} a editar
     */
    private void editarWorkout(EntrenamientoDTO workout) {
        vm.loadWorkoutRelations(workout.getId(), relaciones -> {
            workout.setEntrenamientosEjercicios(relaciones);
            List<Integer> ids = new ArrayList<>();
            for (EntrenamientoEjercicioDTO rel : relaciones) {
                if (rel.getEjercicio() != null && rel.getEjercicio().getIdEjercicio() != null) {
                    ids.add(rel.getEjercicio().getIdEjercicio());
                }
            }
            workout.setEjerciciosId(ids);

            typeVm.getTypes().observe(getViewLifecycleOwner(), tipos -> {
                if (tipos != null && !tipos.isEmpty()) {
                    EditWorkoutDialog.show(this, workout, tipos, updatedWorkout -> {
                        vm.updateWorkout(updatedWorkout.getId(), updatedWorkout,
                            () -> {
                                Toast.makeText(requireContext(), R.string.actualizado, Toast.LENGTH_SHORT).show();
                                vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                            },
                            () -> Toast.makeText(requireContext(), R.string.error_actualizar, Toast.LENGTH_SHORT).show()
                        );
                    });
                } else {
                    Toast.makeText(requireContext(), R.string.error_no_entrenamientos, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Fuerza la recarga de entrenamientos cada vez que el fragmento reanuda.
     * Útil para actualizar la lista si se ha vuelto desde otra pantalla.
     */
    @Override
    public void onResume() {
        super.onResume();
        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);
    }
}
