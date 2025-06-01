package com.proyecto.facilgimapp.ui.types;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.databinding.ItemTypeBinding;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;

/**
 * Adaptador para mostrar y gestionar la lista de tipos de entrenamiento en un RecyclerView.
 * <p>
 * Utiliza ListAdapter con DiffUtil para optimizar cambios. Cada elemento permite
 * editar el tipo mediante clic normal y eliminarlo mediante clic largo.
 * </p>
 * 
 * @author Francisco Santana
 */
public class TypeAdapter extends ListAdapter<TipoEntrenamientoDTO, TypeAdapter.TypeViewHolder> {
    /**
     * Interfaz para manejar interacciones sobre cada tipo:
     * edición y eliminación.
     */
    public interface OnTypeInteractionListener {
        /**
         * Se invoca cuando el usuario desea editar un tipo de entrenamiento.
         *
         * @param type Objeto {@link TipoEntrenamientoDTO} que representa el tipo a editar.
         */
        void onEdit(TipoEntrenamientoDTO type);

        /**
         * Se invoca cuando el usuario desea eliminar un tipo de entrenamiento.
         *
         * @param type Objeto {@link TipoEntrenamientoDTO} que representa el tipo a eliminar.
         */
        void onDelete(TipoEntrenamientoDTO type);
    }

    /**
     * Listener que recibe los eventos de clic y clic largo por cada tipo.
     */
    private final OnTypeInteractionListener listener;

    /**
     * Constructor que inicializa el adaptador con el listener de interacciones.
     *
     * @param listener Implementación de {@link OnTypeInteractionListener} para manejar acciones.
     */
    public TypeAdapter(OnTypeInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    /**
     * Callback utilizado por DiffUtil para determinar si dos elementos representan
     * el mismo tipo o si su contenido ha cambiado.
     */
    private static final DiffUtil.ItemCallback<TipoEntrenamientoDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TipoEntrenamientoDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull TipoEntrenamientoDTO oldItem,
                                               @NonNull TipoEntrenamientoDTO newItem) {
                    // Comparar por ID único
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TipoEntrenamientoDTO oldItem,
                                                  @NonNull TipoEntrenamientoDTO newItem) {
                    // Comparar por nombre para detectar cambios de contenido
                    return oldItem.getNombre().equals(newItem.getNombre());
                }
            };

    /**
     * Infla la vista correspondiente a cada ítem de tipo y crea el ViewHolder.
     *
     * @param parent   Contenedor padre al que pertenecerá el ViewHolder.
     * @param viewType Tipo de vista (no se utiliza aquí, siempre es el mismo layout).
     * @return Nueva instancia de {@link TypeViewHolder}.
     */
    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTypeBinding binding = ItemTypeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TypeViewHolder(binding);
    }

    /**
     * Vincula los datos de un {@link TipoEntrenamientoDTO} al ViewHolder,
     * estableciendo el nombre y configurando los listeners para editar y eliminar.
     *
     * @param holder   ViewHolder que contiene las vistas del ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        TipoEntrenamientoDTO type = getItem(position);
        holder.binding.tvTypeName.setText(type.getNombre());
        holder.binding.getRoot().setOnClickListener(v ->
                listener.onEdit(type)
        );
        holder.binding.getRoot().setOnLongClickListener(v -> {
            listener.onDelete(type);
            return true;
        });
    }

    /**
     * ViewHolder que contiene el binding del layout item_type.xml.
     * Se utiliza para reutilizar vistas eficientemente en el RecyclerView.
     */
    static class TypeViewHolder extends RecyclerView.ViewHolder {
        final ItemTypeBinding binding;

        /**
         * Construye el ViewHolder asociando el binding al itemView raíz.
         *
         * @param binding Binding generado para item_type.xml.
         */
        TypeViewHolder(ItemTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
