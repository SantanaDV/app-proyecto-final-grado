package com.proyecto.facilgimapp.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de ejercicios con opción de selección mediante CheckBox.
 * <p>
 * Cada elemento presenta el nombre y la imagen del ejercicio, y permite marcarlo
 * para seleccionarlo. Mantiene internamente la lista de IDs seleccionados para su posterior uso.
 * </p>
 * 
 * @author Francisco Santana
 */
public class ExerciseSelectionAdapter
        extends RecyclerView.Adapter<ExerciseSelectionAdapter.VH> {

    /**
     * Lista de objetos {@link EjercicioDTO} que se mostrarán en el RecyclerView.
     */
    private final List<EjercicioDTO> exercises = new ArrayList<>();

    /**
     * Lista de IDs de ejercicios que han sido seleccionados por el usuario.
     */
    private final List<Integer> selectedIds = new ArrayList<>();

    /**
     * Establece los IDs que deben aparecer marcados inicialmente cuando se vuelve a mostrar el fragment.
     * Limpia la lista de selecciones y carga los IDs proporcionados.
     * 
     * @param ids Lista de IDs de ejercicios que deben estar marcados; puede ser nula.
     */
    public void setInitiallySelectedIds(List<Integer> ids) {
        selectedIds.clear();
        if (ids != null) selectedIds.addAll(ids);
        notifyDataSetChanged();
    }

    /**
     * Reemplaza la lista de ejercicios actual por una nueva lista y notifica al adaptador
     * para que actualice las vistas.
     * 
     * @param list Lista de {@link EjercicioDTO} que se desea mostrar; debe existir.
     */
    public void setExercises(List<EjercicioDTO> list) {
        exercises.clear();
        exercises.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * Retorna la lista de IDs de ejercicios que están actualmente seleccionados.
     * 
     * @return Lista de enteros que representan los IDs seleccionados.
     */
    public List<Integer> getSelectedExerciseIds() {
        return selectedIds;
    }

    /**
     * Infla la vista correspondiente a cada ítem de ejercicio en modo selección y crea el ViewHolder.
     *
     * @param parent   Contenedor padre al que pertenecerá este ViewHolder.
     * @param viewType Tipo de vista (no se utiliza en este adaptador, siempre es el mismo layout).
     * @return Nueva instancia de {@link VH} con la vista inflada.
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_selection, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de un {@link EjercicioDTO} a las vistas del ViewHolder,
     * cargando el nombre, la imagen y configurando el CheckBox para reflejar y actualizar
     * el estado de selección.
     *
     * @param holder   ViewHolder que contiene las vistas de cada ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(exercises.get(position));
    }

    /**
     * Retorna la cantidad de ejercicios disponibles para mostrar.
     *
     * @return Número total de elementos en la lista de ejercicios.
     */
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada elemento de selección de ejercicio.
     * Se encarga de asignar el nombre, la imagen y manejar los eventos del CheckBox.
     */
    class VH extends RecyclerView.ViewHolder {
        /**
         * Texto que muestra el nombre del ejercicio.
         */
        TextView tvName;

        /**
         * ImageView que muestra la imagen asociada al ejercicio.
         */
        ImageView ivExerciseImage;

        /**
         * CheckBox que indica si el ejercicio está seleccionado o no.
         */
        CheckBox cbSelect;

        /**
         * Constructor que enlaza las vistas del layout item_exercise_selection.xml.
         *
         * @param v Vista inflada correspondiente a un ítem de selección de ejercicio.
         */
        VH(@NonNull View v) {
            super(v);
            tvName          = v.findViewById(R.id.tvExerciseName);
            ivExerciseImage = v.findViewById(R.id.ivExerciseImage);
            cbSelect        = v.findViewById(R.id.cbSelect);
        }

        /**
         * Vincula los datos de un {@link EjercicioDTO} a las vistas y actualiza el estado
         * del CheckBox según la lista de IDs seleccionados. Gestiona los cambios de selección
         * agregando o quitando el ID de la lista interna.
         *
         * @param dto Objeto {@link EjercicioDTO} con la información del ejercicio.
         */
        void bind(EjercicioDTO dto) {
            tvName.setText(dto.getNombre());
            Picasso.get()
                    .load(dto.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(ivExerciseImage);

            // Desvincular listener anterior para evitar llamadas múltiples
            cbSelect.setOnCheckedChangeListener(null);
            boolean isChecked = selectedIds.contains(dto.getIdEjercicio());
            cbSelect.setChecked(isChecked);

            // Actualizar lista de seleccionados cuando cambia el estado del CheckBox
            cbSelect.setOnCheckedChangeListener((button, checked) -> {
                int id = dto.getIdEjercicio();
                if (checked) {
                    if (!selectedIds.contains(id)) selectedIds.add(id);
                } else {
                    selectedIds.remove((Integer) id);
                }
            });
        }
    }
}
