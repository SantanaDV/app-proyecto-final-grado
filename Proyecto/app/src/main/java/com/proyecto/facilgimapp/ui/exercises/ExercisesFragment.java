package com.proyecto.facilgimapp.ui.exercises;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private ActivityResultLauncher<String> pickImageLauncher;
    private ImageView ivPreview;
    private File selectedImageFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Inicializa el selector de imágenes
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null && ivPreview != null) {
                        selectedImageFile = FileUtils.copyUriToFile(requireContext(), uri);
                        Glide.with(requireContext())
                                .load(selectedImageFile)
                                .placeholder(R.drawable.placeholder)
                                .into(ivPreview);
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

        // Configura RecyclerView
        viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
        adapter = new EjercicioCatalogAdapter(this::onLongPressItem);
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);
        binding.rvExercises.setClipToPadding(false);

        // Configura FAB
        FloatingActionButton fab = binding.fabAddExercise;

        boolean admin = SessionManager.isAdmin(requireContext());
        if (admin) {
            fab.setVisibility(View.VISIBLE);
            fab.bringToFront();
            fab.setOnClickListener(v -> showCreateDialog());
        } else {
            fab.setVisibility(View.GONE);
        }

        // Carga datos inicial
        loadExercises();
    }

    private void loadExercises() {
        viewModel.listAllExercises();
        viewModel.getAllExercises()
                .observe(getViewLifecycleOwner(), list ->
                        adapter.submitList(list, true)
                );
    }

    private void onLongPressItem(EjercicioDTO dto) {
        String[] opts = {"Editar", "Eliminar"};
        new AlertDialog.Builder(requireContext())
                .setTitle(dto.getNombre())
                .setItems(opts, (d, which) -> {
                    if (which == 0) showEditDialog(dto);
                    else           confirmDelete(dto);
                })
                .show();
    }

    private void showCreateDialog() {
        View dlg = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_exercise, null);
        EditText etName = dlg.findViewById(R.id.etNombreEjercicio);
        ivPreview = dlg.findViewById(R.id.ivImagenPreview);
        Button btnImg = dlg.findViewById(R.id.btnCargarImagen);

        etName.setText("");
        selectedImageFile = null;
        Glide.with(requireContext())
                .load(R.drawable.placeholder)
                .into(ivPreview);

        btnImg.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        new AlertDialog.Builder(requireContext())
                .setTitle("Crear ejercicio")
                .setView(dlg)
                .setPositiveButton("Crear", (d, w) -> {
                    EjercicioDTO nuevo = new EjercicioDTO();
                    nuevo.setNombre(etName.getText().toString().trim());
                    viewModel.updateExercise(
                            nuevo,
                            selectedImageFile,
                            () -> {
                                Toast.makeText(requireContext(),
                                        "Ejercicio creado",
                                        Toast.LENGTH_SHORT).show();
                                loadExercises();
                            },
                            () -> Toast.makeText(requireContext(),
                                    "Error al crear",
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showEditDialog(EjercicioDTO dto) {
        View dlg = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_exercise, null);
        EditText etName = dlg.findViewById(R.id.etNombreEjercicio);
        ivPreview = dlg.findViewById(R.id.ivImagenPreview);
        Button btnImg = dlg.findViewById(R.id.btnCargarImagen);

        etName.setText(dto.getNombre());
        selectedImageFile = null;
        Glide.with(requireContext())
                .load(dto.getImagenUrl())
                .placeholder(R.drawable.placeholder)
                .into(ivPreview);

        btnImg.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar ejercicio")
                .setView(dlg)
                .setPositiveButton("Guardar", (d, w) -> {
                    dto.setNombre(etName.getText().toString().trim());
                    viewModel.updateExercise(
                            dto,
                            selectedImageFile,
                            () -> {
                                Toast.makeText(requireContext(),
                                        "Actualizado",
                                        Toast.LENGTH_SHORT).show();
                                loadExercises();
                            },
                            () -> Toast.makeText(requireContext(),
                                    "Error al actualizar",
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmDelete(EjercicioDTO dto) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar \"" + dto.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    viewModel.deleteExercise(
                            dto.getIdEjercicio(),
                            () -> {
                                Toast.makeText(requireContext(),
                                        "Ejercicio eliminado",
                                        Toast.LENGTH_SHORT).show();
                                loadExercises();
                            },
                            () -> Toast.makeText(requireContext(),
                                    "Error al eliminar",
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setQueryHint("Buscar...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override
            public boolean onQueryTextChange(String txt) {
                adapter.filter(txt);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ivPreview = null;
        selectedImageFile = null;
    }
}
