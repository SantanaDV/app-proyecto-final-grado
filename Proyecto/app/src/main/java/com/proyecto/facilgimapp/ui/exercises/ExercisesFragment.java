package com.proyecto.facilgimapp.ui.exercises;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentExercisesBinding;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.util.FileUtils;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.ExercisesViewModel;

import java.io.File;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExercisesViewModel viewModel;
    private EjercicioCatalogAdapter adapter;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private ImageView ivPreview;
    private File selectedImageFile; // Campo compartido

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configura el selector de imágenes
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        selectedImageFile = FileUtils.copyUriToFile(requireContext(), uri);

                        if (ivPreview != null) {
                            Glide.with(requireContext())
                                    .load(selectedImageFile)
                                    .placeholder(R.drawable.placeholder)
                                    .into(ivPreview);
                        }
                    }
                }
        );
    }

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

        adapter = new EjercicioCatalogAdapter(this::mostrarOpciones);
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);

        configurarMenu();
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
        });
    }

    private void mostrarOpciones(EjercicioDTO ejercicio) {
        String[] opciones = {"Editar", "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Opciones del ejercicio")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoEdicion(ejercicio);
                    } else {
                        confirmarEliminacion(ejercicio);
                    }
                })
                .show();
    }

    private void mostrarDialogoEdicion(EjercicioDTO ejercicio) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_exercise, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreEjercicio);
        ivPreview = dialogView.findViewById(R.id.ivImagenPreview);
        Button btnCargarImagen = dialogView.findViewById(R.id.btnCargarImagen);

        etNombre.setText(ejercicio.getNombre());
        selectedImageFile = null; // Limpiar selección previa

        // Mostrar imagen actual
        Glide.with(requireContext())
                .load(ejercicio.getImagenUrl())
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .into(ivPreview);

        btnCargarImagen.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar ejercicio")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    ejercicio.setNombre(etNombre.getText().toString());

                    viewModel.updateExercise(
                            ejercicio,
                            selectedImageFile,
                            () -> {
                                Toast.makeText(requireContext(), "Ejercicio actualizado", Toast.LENGTH_SHORT).show();
                                selectedImageFile = null;
                                ivPreview = null;
                                cargarEjercicios();
                            },
                            () -> Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminacion(EjercicioDTO ejercicio) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Estás seguro de que quieres eliminar este ejercicio?\n" +
                        "Si está asociado a entrenamientos, se eliminarán también.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarEjercicio(ejercicio))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarEjercicio(EjercicioDTO ejercicio) {
        viewModel.deleteExercise(
                ejercicio.getIdEjercicio(),
                () -> {
                    Toast.makeText(requireContext(), "Ejercicio eliminado", Toast.LENGTH_SHORT).show();
                    cargarEjercicios();
                },
                () -> Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
        );
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

                searchView.setOnCloseListener(() -> {
                    adapter.filter("");
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
        ivPreview = null;
        selectedImageFile = null;
    }
}
