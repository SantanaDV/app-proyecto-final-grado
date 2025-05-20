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
            Log.d("DEBUG", "Entrenamientos recibidos: " + (list != null ? list.size() : 0));
            adapter.updateList(list);
        });

        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);

        // Agrega aquí la configuración del menú (filtro)
        configurarMenu();

        return b.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón + Nuevo entrenamiento
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

                searchView.setQueryHint("Buscar entrenamientos...");
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
                .setTitle("Descripción")
                .setMessage(workout.getDescripcion())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void confirmarEliminacion(EntrenamientoDTO workout) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Deseas eliminar este entrenamiento?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    vm.deleteWorkout(workout.getId(), () -> {
                        Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                        vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                    }, () -> {
                        Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarWorkout(EntrenamientoDTO workout) {
        // 1) Cargar relaciones ejercicios-series
        vm.loadWorkoutRelations(workout.getId(), relaciones -> {
            workout.setEntrenamientosEjercicios(relaciones);
            // preparar lista ids (igual que ahora)…
            List<Integer> ids = new ArrayList<>();
            for (EntrenamientoEjercicioDTO rel : relaciones) {
                if (rel.getEjercicio() != null && rel.getEjercicio().getIdEjercicio() != null) {
                    ids.add(rel.getEjercicio().getIdEjercicio());
                }
            }
            workout.setEjerciciosId(ids);

            // 2) Observamos la lista de tipos (puede que ya esté cargada)
            typeVm.getTypes().observe(getViewLifecycleOwner(), tipos -> {
                if (tipos != null && !tipos.isEmpty()) {
                    // 3) Llamamos al diálogo pasando workout + tipos
                    EditWorkoutDialog.show(this, workout, tipos, updatedWorkout -> {
                        vm.updateWorkout(updatedWorkout.getId(), updatedWorkout,
                                () -> {
                                    Toast.makeText(requireContext(), "Actualizado", Toast.LENGTH_SHORT).show();
                                    vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                                },
                                () -> Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                        );
                    });
                } else {
                    Toast.makeText(requireContext(),
                            "No hay tipos de entrenamiento disponibles",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void mostrarDialogoEdicion(EntrenamientoDTO workout) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_workout_extended, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etDuracion = dialogView.findViewById(R.id.etDuracion);
        EditText etFecha = dialogView.findViewById(R.id.etFecha);

        etNombre.setText(workout.getNombre());
        etDescripcion.setText(workout.getDescripcion());
        etDuracion.setText(String.valueOf(workout.getDuracion()));
        etFecha.setText(workout.getFechaEntrenamiento().toString());

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar entrenamiento")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    String duracionStr = etDuracion.getText().toString().trim();
                    String fechaStr = etFecha.getText().toString().trim();

                    if (nombre.isEmpty() || duracionStr.isEmpty() || fechaStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        workout.setNombre(nombre);
                        workout.setDescripcion(descripcion);
                        workout.setDuracion(Integer.parseInt(duracionStr));
                        workout.setFechaEntrenamiento(LocalDate.parse(fechaStr));

                        vm.updateWorkout(workout.getId(), workout,
                                () -> {
                                    Toast.makeText(requireContext(), "Actualizado", Toast.LENGTH_SHORT).show();
                                    vm.loadWorkoutsByUserId(SessionManager.getUserId(requireContext()));
                                },
                                () -> Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                        );

                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Datos inválidos", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }




}
