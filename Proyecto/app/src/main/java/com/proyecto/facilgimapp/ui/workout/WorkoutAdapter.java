package com.proyecto.facilgimapp.ui.workout;

import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Adaptador para mostrar una lista de entrenamientos en un RecyclerView.
 * <p>
 * Cada elemento muestra el nombre del entrenamiento y un botón de opciones
 * que permite ver la descripción, editar o eliminar. Incluye funcionalidad de
 * filtrado por nombre y actualización de la lista completa.
 * </p>
 * 
 * @author Francisco Santana
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.H> {

    /**
     * Lista actual de entrenamientos mostrados.
     */
    private final List<EntrenamientoDTO> datos = new ArrayList<>();

    /**
     * Copia completa de todos los entrenamientos para permitir filtrado.
     */
    private final List<EntrenamientoDTO> fullList = new ArrayList<>();

    /**
     * Callback al hacer clic sobre un elemento para ver el detalle.
     */
    private final Consumer<EntrenamientoDTO> onItemClick;

    /**
     * Callback para ver la descripción de un entrenamiento.
     */
    private final Consumer<EntrenamientoDTO> onViewDescription;

    /**
     * Callback para editar un entrenamiento.
     */
    private final Consumer<EntrenamientoDTO> onEdit;

    /**
     * Callback para eliminar un entrenamiento.
     */
    private final Consumer<EntrenamientoDTO> onDelete;

    /**
     * Constructor que inicializa el adaptador con una lista inicial (opcional)
     * y los callbacks para las distintas acciones de usuario.
     *
     * @param d                  Lista inicial de {@link EntrenamientoDTO}; puede ser null.
     * @param onClick            Callback al seleccionar un elemento para ver detalle.
     * @param onViewDescription  Callback para ver descripción.
     * @param onEdit             Callback para editar.
     * @param onDelete           Callback para eliminar.
     */
    public WorkoutAdapter(List<EntrenamientoDTO> d,
                          Consumer<EntrenamientoDTO> onClick,
                          Consumer<EntrenamientoDTO> onViewDescription,
                          Consumer<EntrenamientoDTO> onEdit,
                          Consumer<EntrenamientoDTO> onDelete) {
        this.onItemClick = onClick;
        this.onViewDescription = onViewDescription;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        if (d != null) {
            datos.addAll(d);
            fullList.addAll(d);
        }
    }

    /**
     * Reemplaza la lista actual de entrenamientos por la proporcionada
     * (tanto en datos como en fullList) y notifica el cambio para refrescar la vista.
     *
     * @param list Nueva lista de {@link EntrenamientoDTO}; puede ser null para vaciar.
     */
    public void updateList(List<EntrenamientoDTO> list) {
        datos.clear();
        fullList.clear();
        if (list != null) {
            datos.addAll(list);
            fullList.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * Filtra la lista de entrenamientos por nombre que contenga la cadena dada.
     * <p>
     * Si el query es nulo o vacío, restaura la lista completa.
     * </p>
     *
     * @param query Texto de búsqueda; se compara en minúsculas con el nombre.
     */
    public void filter(String query) {
        datos.clear();
        if (query == null || query.trim().isEmpty()) {
            datos.addAll(fullList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (EntrenamientoDTO e : fullList) {
                if (e.getNombre().toLowerCase().contains(lowerQuery)) {
                    datos.add(e);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Infla la vista de cada elemento de entrenamiento.
     *
     * @param parent   Contenedor padre donde se inflará la vista.
     * @param viewType Tipo de vista; no se utiliza (siempre el mismo layout).
     * @return Nueva instancia de {@link H}.
     */
    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new H(v);
    }

    /**
     * Vincula los datos de un {@link EntrenamientoDTO} al ViewHolder:
     * <ul>
     *   <li>Establece el nombre en un TextView.</li>
     *   <li>Configura el clic en la tarjeta para ver detalle.</li>
     *   <li>Configura el menú de opciones (ver descripción, editar, eliminar).</li>
     * </ul>
     *
     * @param holder ViewHolder que contiene las vistas de cada ítem.
     * @param pos    Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull H holder, int pos) {
        EntrenamientoDTO e = datos.get(pos);
        Integer idEntrenamiento = e.getId();

        if (idEntrenamiento != null) {
            holder.tv.setText(e.getNombre());

            // Clic en la tarjeta para ver detalle
            holder.itemView.setOnClickListener(v -> {
                Log.d("DEBUG", "ID del entrenamiento: " + idEntrenamiento);
                onItemClick.accept(e);
            });

            // Botón de opciones con PopupMenu
            holder.btnOptions.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.menu_workout_item);

                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.action_view_description) {
                        onViewDescription.accept(e);
                        return true;
                    } else if (id == R.id.action_edit) {
                        onEdit.accept(e);
                        return true;
                    } else if (id == R.id.action_delete) {
                        onDelete.accept(e);
                        return true;
                    }

                    return false;
                });

                popup.show();
            });

        } else {
            Log.e("DEBUG", "ID del entrenamiento es nulo para: " + e.getNombre());
            holder.tv.setText(R.string.entrenamiento_no_disponible);
        }
    }

    /**
     * Retorna la cantidad de entrenamientos en la lista actual.
     *
     * @return Número de elementos en {@code datos}.
     */
    @Override
    public int getItemCount() {
        return datos.size();
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada elemento de entrenamiento:
     * un TextView para el nombre y un ImageButton para las opciones.
     */
    static class H extends RecyclerView.ViewHolder {
        TextView tv;
        ImageButton btnOptions;

        /**
         * Constructor que enlaza las vistas definidas en item_workout.xml.
         *
         * @param v Vista inflada de cada ítem de entrenamiento.
         */
        H(View v) {
            super(v);
            tv = v.findViewById(R.id.tvWorkoutName);
            btnOptions = v.findViewById(R.id.btnOptions);
        }
    }
}
