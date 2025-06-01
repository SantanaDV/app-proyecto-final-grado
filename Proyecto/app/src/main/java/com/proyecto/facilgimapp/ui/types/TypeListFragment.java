package com.proyecto.facilgimapp.ui.types;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentTypeListBinding;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;

/**
 * Fragment que muestra la lista de tipos de entrenamiento y permite
 * crear, editar o eliminar cada tipo.
 * <p>
 * Utiliza un RecyclerView con {@link TypeAdapter} para presentar los elementos.
 * Al pulsar el FloatingActionButton se abre un diálogo para añadir un nuevo tipo,
 * y mediante clic o clic largo en cada elemento se editan o eliminan respectivamente.
 * </p>
 * 
 * @author Francisco Santana
 */
public class TypeListFragment extends Fragment implements TypeAdapter.OnTypeInteractionListener {
    /**
     * Binding generado para acceder a las vistas definidas en fragment_type_list.xml.
     */
    private FragmentTypeListBinding binding;

    /**
     * ViewModel que maneja la lógica de negocio de tipos de entrenamiento.
     */
    private TypeViewModel viewModel;

    /**
     * Adaptador para mostrar la lista de {@link TipoEntrenamientoDTO}.
     */
    private TypeAdapter adapter;

    /**
     * Infla el layout del fragment y devuelve la vista raíz.
     *
     * @param inflater           Inflador de vistas de Android.
     * @param container          Contenedor padre en el que se insertará el fragmento.
     * @param savedInstanceState Bundle con el estado anterior del fragmento; puede ser null.
     * @return Vista raíz inflada correspondiente al layout fragment_type_list.xml.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTypeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>Inicialización de {@link TypeViewModel}.</li>
     *     <li>RecyclerView con {@link TypeAdapter} en disposición vertical.</li>
     *     <li>Configuración del FloatingActionButton para abrir el diálogo de creación.</li>
     *     <li>Observador de LiveData para recibir la lista de tipos y actualizar el adaptador.</li>
     *     <li>Llamada a {@link TypeViewModel#loadTypes()} para cargar los datos.</li>
     * </ul>
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado anterior; puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TypeViewModel.class);
        adapter = new TypeAdapter(this);

        binding.rvTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTypes.setAdapter(adapter);

        // Botón flotante para crear un nuevo tipo de entrenamiento
        binding.fabNewWorkout.setOnClickListener(v -> showAddOrEditDialog(null));

        // Observador para actualizar la lista cuando cambien los datos
        viewModel.getTypes().observe(getViewLifecycleOwner(), types -> adapter.submitList(types));
        viewModel.loadTypes();
    }

    /**
     * Muestra un diálogo para añadir un nuevo tipo o editar uno existente.
     * <p>
     * Si editType es null, el título será "Nuevo tipo" y al confirmar se llamará a
     * {@link TypeViewModel#addType(String)}. Si no es null, el diálogo precarga el nombre
     * y al confirmar se llamará a {@link TypeViewModel#updateType(int, String)}.
     * </p>
     *
     * @param editType DTO de {@link TipoEntrenamientoDTO} a editar; si es null, crea uno nuevo.
     */
    private void showAddOrEditDialog(@Nullable TipoEntrenamientoDTO editType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_type, null);
        EditText etName = dialogView.findViewById(R.id.etTypeName);
        if (editType != null) {
            etName.setText(editType.getNombre());
        }

        builder.setTitle(editType == null
                        ? getString(R.string.nuevo_tipo)
                        : getString(R.string.editar_tipo))
                .setView(dialogView)
                .setPositiveButton(editType == null
                        ? getString(R.string.crear)
                        : getString(R.string.action_save), null)
                .setNegativeButton(getString(R.string.action_cancel), null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = etName.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError(getString(R.string.error_required));
                    return;
                }

                if (editType == null) {
                    viewModel.addType(name);
                    Toast.makeText(requireContext(), R.string.tipo_creado, Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.updateType(editType.getId(), name);
                    Toast.makeText(requireContext(), R.string.tipo_actualizado, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    /**
     * Se invoca cuando el usuario solicita editar un tipo de entrenamiento
     * haciendo clic en un elemento de la lista. Abre el diálogo con el tipo seleccionado.
     *
     * @param type DTO de {@link TipoEntrenamientoDTO} que se desea editar.
     */
    @Override
    public void onEdit(TipoEntrenamientoDTO type) {
        showAddOrEditDialog(type);
    }

    /**
     * Se invoca cuando el usuario solicita eliminar un tipo de entrenamiento
     * haciendo clic largo en un elemento de la lista. Muestra un diálogo de confirmación,
     * y al aceptar llama a {@link TypeViewModel#deleteType(int, TypeViewModel.DeletionCallback)}.
     *
     * @param type DTO de {@link TipoEntrenamientoDTO} que se desea eliminar.
     */
    @Override
    public void onDelete(TipoEntrenamientoDTO type) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.eliminar_tipo)
                .setMessage("¿" + getString(R.string.eliminar) + " “" + type.getNombre() + "”?")
                .setPositiveButton(getString(R.string.eliminar), (d, w) -> {
                    viewModel.deleteType(type.getId(), new TypeViewModel.DeletionCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(requireContext(),
                                    R.string.tipo_eliminado_correctamente,
                                    Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
