package com.proyecto.facilgimapp.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adaptador para mostrar un catálogo de ejercicios en un RecyclerView.
 * <p>
 * Utiliza ListAdapter con DiffUtil para optimizar actualizaciones parciales.
 * Mantiene una lista completa (fullList) para poder aplicar filtros sin perder datos.
 * Soporta clic largo en elementos para acciones administrativas (ROLE_ADMIN).
 * </p>
 * 
 * @author Francisco Santana
 */
public class EjercicioCatalogAdapter
        extends ListAdapter<EjercicioDTO, EjercicioCatalogAdapter.ViewHolder> {

    /**
     * Interfaz para manejar eventos de clic largo en un ejercicio.
     */
    public interface OnLongItemClick {
        /**
         * Se invoca cuando el usuario realiza clic largo sobre un ejercicio.
         *
         * @param ejercicio Datos del ejercicio sobre el que se hizo clic largo.
         */
        void onLongClick(EjercicioDTO ejercicio);
    }

    /**
     * Listener que recibe el evento de clic largo para un elemento.
     */
    private final OnLongItemClick longClickListener;

    /**
     * Lista completa de ejercicios para restaurar datos al filtrar.
     */
    private final List<EjercicioDTO> fullList = new ArrayList<>();

    /**
     * Constructor que inicializa el adaptador con el listener de clic largo
     * y habilita IDs estables para optimizar animaciones y actualizaciones.
     *
     * @param longClickListener Implementación de {@link OnLongItemClick} para manejar clic largo.
     */
    public EjercicioCatalogAdapter(OnLongItemClick longClickListener) {
        super(DIFF_CALLBACK);
        this.longClickListener = longClickListener;
        // Habilitamos el uso de IDs estables
        setHasStableIds(true);
    }

    /**
     * Retorna el ID estable de un elemento basado en su identificador de ejercicio.
     *
     * @param position Posición del elemento en la lista.
     * @return ID único (idEjercicio) del {@link EjercicioDTO} en la posición indicada.
     */
    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(getItem(position)).getIdEjercicio();
    }

    /**
     * Infla la vista correspondiente a cada ítem de ejercicio y crea el ViewHolder.
     *
     * @param parent   Contenedor padre al que pertenecerá este ViewHolder.
     * @param viewType Tipo de vista (no se utiliza en este adaptador, siempre es el mismo layout).
     * @return Nueva instancia de {@link ViewHolder} con la vista inflada.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula los datos completos de un {@link EjercicioDTO} al ViewHolder,
     * configurando nombre, imagen y listener de clic largo si corresponde.
     *
     * @param holder   ViewHolder que contiene las vistas del ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), longClickListener);
    }

    /**
     * Variante de onBindViewHolder que permite actualizaciones parciales mediante payloads.
     * Si no hay payloads, delega al bind completo. Si existen, actualiza solo los campos cambiados:
     * nombre e imagen.
     *
     * @param holder   ViewHolder que contiene las vistas del ítem.
     * @param position Posición del elemento en la lista.
     * @param payloads Lista de objetos con cambios parciales (Bundle con claves "nombre" o "imagenUrl").
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Si no hay payloads, bind normal
            onBindViewHolder(holder, position);
        } else {
            // Actualizaciones parciales
            EjercicioDTO dto = getItem(position);
            for (Object pl : payloads) {
                if (pl instanceof Bundle) {
                    Bundle b = (Bundle) pl;
                    if (b.containsKey("nombre")) {
                        holder.tvName.setText(b.getString("nombre"));
                    }
                    if (b.containsKey("imagenUrl")) {
                        String url = b.getString("imagenUrl");
                        if (url != null && !url.isEmpty()) {
                            Glide.with(holder.ivImage.getContext())
                                    .asDrawable()
                                    .load(url)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .centerCrop()
                                    .into(holder.ivImage);
                        } else {
                            holder.ivImage.setImageResource(R.drawable.placeholder);
                        }
                    }
                }
            }
        }
    }

    /**
     * Reemplaza la lista interna utilizada por el ListAdapter y, si es fullUpdate,
     * actualiza también fullList para mantener copia completa.
     *
     * @param list         Lista de {@link EjercicioDTO} que se mostrará.
     * @param isFullUpdate Indica si se debe refrescar fullList con la nueva lista.
     */
    public void submitList(List<EjercicioDTO> list, boolean isFullUpdate) {
        super.submitList(list, () -> {
            if (isFullUpdate) {
                fullList.clear();
                fullList.addAll(list);
            }
        });
    }

    /**
     * Filtra los ejercicios cuyo nombre contiene el texto proporcionado (insensible a mayúsculas).
     * Si la consulta es nula o vacía, restaura la lista completa.
     *
     * @param query Texto a buscar en el nombre del ejercicio; puede ser null o vacío.
     */
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            submitList(new ArrayList<>(fullList), false);
            return;
        }
        List<EjercicioDTO> filtered = new ArrayList<>();
        for (EjercicioDTO e : fullList) {
            if (e.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        submitList(filtered, false);
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada elemento de ejercicio.
     * Se encarga de asignar nombre, cargar imagen y configurar el listener de clic largo
     * si el usuario tiene rol de administrador.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView para mostrar la imagen del ejercicio.
         */
        final ImageView ivImage;

        /**
         * TextView para mostrar el nombre del ejercicio.
         */
        final TextView tvName;

        /**
         * Constructor que enlaza las vistas del layout item_exercise.xml.
         *
         * @param itemView Vista inflada correspondiente a un ítem de ejercicio.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imgExercise);
            tvName  = itemView.findViewById(R.id.tvExerciseName);
        }

        /**
         * Vincula los datos de un {@link EjercicioDTO} a las vistas y establece el listener
         * de clic largo si el usuario tiene permiso de administrador.
         *
         * @param dto               Objeto con datos del ejercicio a mostrar.
         * @param longClickListener Listener para manejar clic largo en el elemento.
         */
        void bind(EjercicioDTO dto, OnLongItemClick longClickListener) {
            tvName.setText(dto.getNombre());
            String url = dto.getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivImage.getContext())
                        .asDrawable()
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder);
            }

            // Solo administradores pueden realizar clic largo para acciones adicionales
            if (SessionManager.getAuthorities(ivImage.getContext())
                    .contains("ROLE_ADMIN")) {
                itemView.setOnLongClickListener(v -> {
                    longClickListener.onLongClick(dto);
                    return true;
                });
            }
        }
    }

    /**
     * Callback utilizado por DiffUtil para determinar si dos objetos {@link EjercicioDTO}
     * representan el mismo elemento y si su contenido ha cambiado.
     * Soporta payloads para actualizaciones parciales de nombre e imagen.
     */
    private static final DiffUtil.ItemCallback<EjercicioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<EjercicioDTO>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull EjercicioDTO a,
                        @NonNull EjercicioDTO b) {
                    return Objects.equals(a.getIdEjercicio(), b.getIdEjercicio());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull EjercicioDTO a,
                        @NonNull EjercicioDTO b) {
                    return Objects.equals(a.getNombre(),   b.getNombre())
                            && Objects.equals(a.getImagenUrl(), b.getImagenUrl());
                }

                @Override
                @Nullable
                public Object getChangePayload(
                        @NonNull EjercicioDTO oldItem,
                        @NonNull EjercicioDTO newItem) {
                    Bundle diff = new Bundle();
                    if (!Objects.equals(oldItem.getNombre(), newItem.getNombre())) {
                        diff.putString("nombre", newItem.getNombre());
                    }
                    if (!Objects.equals(oldItem.getImagenUrl(), newItem.getImagenUrl())) {
                        diff.putString("imagenUrl", newItem.getImagenUrl());
                    }
                    return diff.size() == 0 ? null : diff;
                }
            };
}
