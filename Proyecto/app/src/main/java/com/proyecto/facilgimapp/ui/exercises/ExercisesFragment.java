package com.proyecto.facilgimapp.ui.exercises;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentExercisesBinding;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.repository.EjercicioRepository;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExercisesViewModel viewModel;
    private EjercicioCatalogAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Ejercicios");

        viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);

        adapter = new EjercicioCatalogAdapter(ejercicioDTO -> confirmarEliminacion(ejercicioDTO));

        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);

        // Buscar en Toolbar
        configurarMenu();

        // Cargar ejercicios
        cargarEjercicios();

        if (SessionManager.getAuthorities(requireContext()).contains("ROLE_ADMIN")) {
            binding.fabAddExercise.setVisibility(View.VISIBLE);
            binding.fabAddExercise.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Aquí iría la creación", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void cargarEjercicios() {
        viewModel.listAllExercises();
        viewModel.getAllExercises().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list, true);
        });    }
    private void confirmarEliminacion(EjercicioDTO ejercicio) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Estás seguro de que quieres eliminar este ejercicio?\n" +
                        "Si está asociado a entrenamientos, se eliminarán los ejercicios de ese entrenamiento.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarEjercicio(ejercicio))
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void eliminarEjercicio(EjercicioDTO ejercicio) {
        EjercicioRepository repo = new EjercicioRepository(requireContext());
        repo.deleteExercise(ejercicio.getIdEjercicio()).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call,
                                   @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Ejercicio eliminado", Toast.LENGTH_SHORT).show();
                    cargarEjercicios();
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
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

                searchView.setQueryHint("Buscar ejercicios...");
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
                        return true;
                    }
                });

                //  Restaurar la lista completa al cerrar el SearchView
                searchView.setOnCloseListener(() -> {
                    adapter.filter(""); // vacía el texto, fuerza el reset
                    return false;
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
