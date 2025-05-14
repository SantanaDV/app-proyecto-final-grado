package com.proyecto.facilgimapp.ui.exercises;

import android.os.Bundle;
import android.view.*;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
    private EjercicioDTOAdapter adapter;

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

        adapter = new EjercicioDTOAdapter(
                ejercicioId -> {
                    Bundle args = new Bundle();
                    args.putInt("exerciseId", ejercicioId);
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_exercisesFragment_to_workoutSessionFragment, args);
                },
                ejercicioDTO -> eliminarEjercicio(ejercicioDTO)
        );

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
        String username = SessionManager.getUsername(requireContext());
        int idUser = SessionManager.getUserId(requireContext());

        viewModel.loadExercises(idUser, username);
        viewModel.getExercises().observe(getViewLifecycleOwner(), adapter::submitList);
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
                SearchView searchView = (SearchView) searchItem.getActionView();

                searchView.setQueryHint("Buscar ejercicios...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
