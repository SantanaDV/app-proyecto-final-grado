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

/**
 * Fragment que muestra el catálogo de ejercicios, permitiendo buscarlos, filtrarlos,
 * crearlos, editar y eliminar (solo para administradores). Utiliza un RecyclerView
 * con {@link EjercicioCatalogAdapter} para presentar la lista, y un FAB para abrir
 * diálogos de creación o edición de ejercicios con carga de imágenes.
 * <p>
 * Soporta selección de imagen mediante un {@link ActivityResultLauncher} y gestiona
 * la obtención del nombre real de un URI para mantener la extensión correcta.
 * </p>
 *
 * @author Francisco Santana
 */
public class ExercisesFragment extends Fragment {

    /**
     * Binding generado para acceder a las vistas del layout fragment_exercises.xml.
     */
    private FragmentExercisesBinding binding;

    /**
     * ViewModel que gestiona la lógica de negocio y la comunicación con el repositorio
     * para obtener, crear, actualizar y eliminar ejercicios.
     */
    private ExercisesViewModel viewModel;

    /**
     * Adaptador para presentar el catálogo de ejercicios en el RecyclerView,
     * con soporte para filtro y clic largo.
     */
    private EjercicioCatalogAdapter adapter;

    /**
     * Lanzador para seleccionar imágenes desde el almacenamiento del dispositivo.
     */
    private ActivityResultLauncher<String> pickImageLauncher;

    /**
     * Vista previa de la imagen seleccionada en los diálogos de creación/edición.
     */
    private ImageView ivPreview;

    /**
     * Archivo temporal que almacena la imagen seleccionada antes de enviarla al servidor.
     */
    private File selectedImageFile;

    /**
     * Se ejecuta al crear el fragment y habilita el menú de opciones. Inicializa
     * el {@link ActivityResultLauncher} para selección de imágenes, que:
     * <ul>
     *     <li>Extrae el nombre real con extensión </li>
     *     <li>Copia el contenido a un File en cacheDir</li>
     *     <li>Muestra la vista previa con Glide</li>
     * </ul>
     *
     * @param savedInstanceState Bundle con el estado previo, puede ser null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Inicializa el selector de imágenes
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null && ivPreview != null) {
                        // Extrae el nombre real con extensión
                        String realName = getFileNameWithExtensionFromUri(requireContext(), uri);
                        // Copia a un File en cacheDir preservando la extensión
                        selectedImageFile = FileUtils.copyUriToFile(requireContext(), uri, realName);

                        // Carga la vista previa
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

    /**
     * Infla el layout del fragment y devuelve la vista raíz para su renderizado.
     *
     * @param inflater           Inflador de vistas.
     * @param container          Contenedor padre para la vista.
     * @param savedInstanceState Bundle con el estado previo, puede ser null.
     * @return Vista raíz inflada del fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>RecyclerView con {@link EjercicioCatalogAdapter} y LayoutManager</li>
     *     <li>FloatingActionButton para crear ejercicios (visible solo si es administrador)</li>
     *     <li>Carga inicial de la lista de ejercicios desde el ViewModel</li>
     * </ul>
     *
     * @param view               Vista previamente inflada.
     * @param savedInstanceState Bundle con el estado previo, puede ser null.
     */
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

        // Configura el FAB (solo visible para administradores)
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

    /**
     * Solicita al ViewModel la lista completa de ejercicios y observa los cambios
     * para actualizar el adaptador. Usa {@link EjercicioCatalogAdapter#submitList(List, boolean)}
     * indicando fullUpdate=true para mantener copia completa.
     */
    private void loadExercises() {
        viewModel.listAllExercises();
        viewModel.getAllExercises()
                .observe(getViewLifecycleOwner(), list ->
                        adapter.submitList(list, true)
                );
    }

    /**
     * Manejador llamado cuando se realiza clic largo sobre un elemento.
     * Muestra un diálogo con opciones "Editar" y "Eliminar".
     *
     * @param dto {@link EjercicioDTO} del ejercicio seleccionado.
     */
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

    /**
     * Muestra un diálogo para crear un nuevo ejercicio. Contiene:
     * <ul>
     *     <li>Campo para nombre de ejercicio</li>
     *     <li>Botón para cargar imagen (lanza pickImageLauncher)</li>
     *     <li>Vista previa de la imagen seleccionada</li>
     * </ul>
     * Al confirmar, construye un {@link EjercicioDTO} con el nombre ingresado y llama
     * a {@link ExercisesViewModel#updateExercise(EjercicioDTO, File, Runnable, Runnable)}
     * pasando la imagen seleccionada. En caso de éxito, muestra Toast y recarga ejercicios;
     * en caso de error, muestra Toast de error.
     */
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

    /**
     * Muestra un diálogo para editar un ejercicio existente. Precarga:
     * <ul>
     *     <li>Nombre actual en el EditText</li>
     *     <li>Imagen actual en la vista previa</li>
     * </ul>
     * Permite cambiar el nombre y seleccionar una nueva imagen mediante pickImageLauncher.
     * Al confirmar, actualiza el DTO y llama a {@link ExercisesViewModel#updateExercise(EjercicioDTO, File, Runnable, Runnable)}.
     * En caso de éxito, muestra Toast, vuelve a cargar la lista y recrea el fragment;
     * en caso de error, muestra Toast de fallo.
     *
     * @param dto {@link EjercicioDTO} del ejercicio a editar.
     */
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

    /**
     * Muestra un diálogo de confirmación para eliminar un ejercicio. Si el usuario confirma,
     * llama a {@link ExercisesViewModel#deleteExercise(int, Runnable, Runnable)} con callbacks
     * para mostrar Toast y recargar la lista o mostrar error.
     *
     * @param dto {@link EjercicioDTO} del ejercicio a eliminar.
     */
    private void confirmDelete(EjercicioDTO dto) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.eliminar)
                .setMessage("¿"+ getString(R.string.eliminar) + " \"" + dto.getNombre() + "\"?")
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

    /**
     * Infla el menú de opciones, configurando un SearchView que filtra la lista de ejercicios
     * mediante {@link EjercicioCatalogAdapter#filter(String)} a medida que el texto cambia.
     *
     * @param menu     Menú donde se inflará el XML menu_search.
     * @param inflater Inflador de menús.
     */
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
     * Intenta extraer el nombre real (con extensión) de un {@link Uri}:
     * <ul>
     *     <li>Si el scheme es "content://", consulta OpenableColumns.DISPLAY_NAME.</li>
     *     <li>Si no existe DISPLAY_NAME, toma el fragmento después de la última '/'.</li>
     *     <li>Si el nombre no contiene punto, infiere la extensión a partir del MIME y la añade.</li>
     * </ul>
     *
     * @param ctx Contexto para acceder al ContentResolver.
     * @param uri {@link Uri} del que se desea obtener el nombre con extensión.
     * @return Nombre con extensión inferida si era necesario.
     */
    private String getFileNameWithExtensionFromUri(@NonNull Context ctx, @NonNull Uri uri) {
        String name = null;

        // Si es un Content Uri, intentamos leer DISPLAY_NAME
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

        // Si aún no tenemos nombre, tomamos la parte después de la última '/'
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

    /**
     * Se llama al reanudar el fragment. Refresca la lista de ejercicios
     * para reflejar cambios realizados fuera de esta vista.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadExercises();
    }

    /**
     * Se llama cuando la vista del fragmento se destruye. Libera referencias
     * a binding, vista previa e imagen seleccionada para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ivPreview = null;
        selectedImageFile = null;
    }
}
