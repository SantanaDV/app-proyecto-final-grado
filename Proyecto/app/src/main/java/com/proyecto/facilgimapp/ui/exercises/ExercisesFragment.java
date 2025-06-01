package com.proyecto.facilgimapp.ui.exercises;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
                        //  Extrae el nombre real con extensión
                        String realName = getFileNameWithExtensionFromUri(requireContext(), uri);
                        // Copia a un File en cacheDir preservando la extensión
                        selectedImageFile = FileUtils.copyUriToFile(requireContext(), uri, realName);

                        //  Carga la vista previa
                        Glide.with(requireContext())
                                .asDrawable()
                                .load(selectedImageFile)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
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

        // Configura el RecyclerView
        viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
        adapter = new EjercicioCatalogAdapter(this::onLongPressItem);
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);
        binding.rvExercises.setClipToPadding(false);

        // Configura el FAB
        FloatingActionButton fab = binding.fabAddExercise;

        boolean admin = SessionManager.isAdmin(requireContext());
        if (admin) {
            fab.setVisibility(View.VISIBLE);
            fab.bringToFront();
            fab.setOnClickListener(v -> showCreateDialog());
        } else {
            fab.setVisibility(View.GONE);
        }

        // Carga de datos inicial
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
        String[] opts = {
                getString(R.string.editar),
                getString(R.string.eliminar)
        };
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
                .asDrawable()
                .load(R.drawable.placeholder)
                .into(ivPreview);

        btnImg.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.cear_ejercicio)
                .setView(dlg)
                .setPositiveButton(R.string.crear, (d, w) -> {
                    EjercicioDTO nuevo = new EjercicioDTO();
                    nuevo.setNombre(etName.getText().toString().trim());
                    viewModel.updateExercise(
                            nuevo,
                            selectedImageFile,
                            () -> {
                                Toast.makeText(requireContext(),
                                        R.string.ejercicio_creado,
                                        Toast.LENGTH_SHORT).show();
                                loadExercises();
                            },
                            () -> Toast.makeText(requireContext(),
                                    R.string.error_crear_ejercicio,
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton(R.string.action_cancel, null)
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
                .error(R.drawable.placeholder)
                .into(ivPreview);

        btnImg.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_edit_exercise)
                .setView(dlg)
                .setPositiveButton(R.string.action_save, (d, w) -> {
                    dto.setNombre(etName.getText().toString().trim());
                    viewModel.updateExercise(
                            dto,
                            selectedImageFile,
                            () -> {
                                Toast.makeText(requireContext(),
                                        R.string.ejercicio_actualizado,
                                        Toast.LENGTH_SHORT).show();

                                // 1) Refresca los datos en el ViewModel
                                viewModel.listAllExercises();

                                // 2) Recrea este fragment
                                getParentFragmentManager()
                                        .beginTransaction()
                                        .detach(ExercisesFragment.this)
                                        .attach(ExercisesFragment.this)
                                        .commit();
                            },
                            () -> Toast.makeText(requireContext(),
                                    R.string.error_actualizar,
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }



    private void confirmDelete(EjercicioDTO dto) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.eliminar)
                .setMessage("¿"+ getString(R.string.eliminar) + " "  +"\"" + dto.getNombre() + "\"?")
                .setPositiveButton(R.string.eliminar , (d, w) -> {
                    viewModel.deleteExercise(
                            dto.getIdEjercicio(),
                            () -> {
                                Toast.makeText(requireContext(),
                                        R.string.eliminar_ejercicio,
                                        Toast.LENGTH_SHORT).show();
                                loadExercises();
                            },
                            () -> Toast.makeText(requireContext(),
                                    R.string.error_al_eliminar,
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setQueryHint(getString(R.string.buscar));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override
            public boolean onQueryTextChange(String txt) {
                adapter.filter(txt);
                return true;
            }
        });
    }


        /**
         * Intenta extraer el nombre real (con extensión) de un Uri
         *  Si el scheme es "content://", consulta OpenableColumns.DISPLAY_NAME.
         *  Si no hay DISPLAY_NAME o no es scheme "content", toma el fragmento después de la última '/'.
         *  Si ese nombre no contiene punto (sin extensión), extrae el mime-type y añade la extensión correspondiente.
         */
        private String getFileNameWithExtensionFromUri(@NonNull Context ctx, @NonNull Uri uri) {
            String name = null;

            //  Si es un Content Uri, intentamos leer DISPLAY_NAME
            if ("content".equals(uri.getScheme())) {
                Cursor cursor = null;
                try {
                    cursor = ctx.getContentResolver()
                            .query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (idx >= 0) {
                            name = cursor.getString(idx);
                        }
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }

            //  Si aún no tenemos nombre, tomamos la parte después de la última '/'
            if (name == null) {
                String path = uri.getPath();
                if (path != null) {
                    int cut = path.lastIndexOf('/');
                    if (cut != -1) {
                        name = path.substring(cut + 1);
                    } else {
                        name = path;
                    }
                }
            }

            if (name == null) {
                name = "tmpfile"; // fallback si no se consigue nada
            }

            // Si el nombre no contiene punto, inferimos la extensión por MIME
            if (!name.contains(".")) {
                String mime = ctx.getContentResolver().getType(uri); // ej: "image/gif"
                String ext = null;
                if (mime != null) {
                    ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime); // ej: "gif", "png"
                }
                if (ext == null) {
                    ext = ""; // si no sabemos, lo dejamos vacío
                } else {
                    ext = "." + ext;
                }
                name = name + ext;
            }

            return name;
        }




    @Override
    public void onResume() {
        super.onResume();
        loadExercises();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ivPreview = null;
        selectedImageFile = null;
    }
}
