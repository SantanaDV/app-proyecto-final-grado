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

public class WorkoutsFragment extends Fragment {
    private FragmentWorkoutsBinding b;
    private WorkoutAdapter adapter;

    private TypeViewModel typeVm;


    private WorkoutViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutsBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(WorkoutViewModel.class);

        typeVm = new ViewModelProvider(this).get(TypeViewModel.class);
        typeVm.loadTypes();  // dispara la carga de tipos

        adapter = new WorkoutAdapter(
                new ArrayList<>(),
                workout -> {
                    Bundle args = new Bundle();
                    args.putInt("workoutId", workout.getId());
                    Navigation.findNavController(b.getRoot())
                            .navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, args);
                },
                workout -> mostrarDescripcion(workout),
                workout -> editarWorkout(workout),
                workout -> confirmarEliminacion(workout)
        );


        b.rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvWorkouts.setAdapter(adapter);

        vm.getWorkouts().observe(getViewLifecycleOwner(), list -> {
            adapter.updateList(list);
            boolean empty = list == null || list.isEmpty();
            b.tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.rvWorkouts.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);
        configurarMenu();
        return b.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón Nuevo entrenamiento
        b.fabNewWorkout.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_workoutsFragment_to_newWorkoutFragment)
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }

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
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void mostrarDescripcion(EntrenamientoDTO workout) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.descripcion)
                .setMessage(workout.getDescripcion())
                .setPositiveButton(R.string.close, null)
                .show();
    }

    private void confirmarEliminacion(EntrenamientoDTO workout) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.eliminar)
                .setMessage(R.string.eliminar_entrenamiento)
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    vm.deleteWorkout(workout.getId(), () -> {
                        Toast.makeText(getContext(), R.string.eliminado, Toast.LENGTH_SHORT).show();
                        vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                    }, () -> {
                        Toast.makeText(getContext(), R.string.error_al_eliminar, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void editarWorkout(EntrenamientoDTO workout) {
        // Carga las relaciones ejercicios-series
        vm.loadWorkoutRelations(workout.getId(), relaciones -> {
            workout.setEntrenamientosEjercicios(relaciones);
            // prepara la lista ids
            List<Integer> ids = new ArrayList<>();
            for (EntrenamientoEjercicioDTO rel : relaciones) {
                if (rel.getEjercicio() != null && rel.getEjercicio().getIdEjercicio() != null) {
                    ids.add(rel.getEjercicio().getIdEjercicio());
                }
            }
            workout.setEjerciciosId(ids);

            // Observamos la lista de tipos
            typeVm.getTypes().observe(getViewLifecycleOwner(), tipos -> {
                if (tipos != null && !tipos.isEmpty()) {
                    // Llamamos al diálogo pasando workout + tipos
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
                    Toast.makeText(requireContext(),
                            R.string.error_no_entrenamientos,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fuerza recarga cada vez que regresamos aquí
        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);
    }





}
