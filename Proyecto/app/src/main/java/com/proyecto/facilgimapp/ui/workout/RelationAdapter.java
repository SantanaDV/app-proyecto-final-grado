package com.proyecto.facilgimapp.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

import java.util.Objects;

/**
 * Adaptador que muestra la relación ejercicio–serie dentro de un entrenamiento.
 * <p>
 * Cada elemento presenta el nombre del ejercicio, su imagen y un conjunto de chips
 * que indican las series (repeticiones × peso). También muestra la cantidad total
 * de series.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class RelationAdapter
        extends ListAdapter<EntrenamientoEjercicioDTO, RelationAdapter.ViewHolder> {

    /**
     * Fecha del entrenamiento, usada solo para referencia en cada ítem.
     */
    private final String workoutDate;

    /**
     * Duración en minutos del entrenamiento, usada solo para referencia en cada ítem.
     */
    private final int workoutDurationMin;

    /**
     * Constructor que inicializa el adaptador con la fecha y duración del entrenamiento.
     *
     * @param workoutDate        Cadena con la fecha del entrenamiento.
     * @param workoutDurationMin Duración en minutos del entrenamiento.
     */
    public RelationAdapter(String workoutDate, int workoutDurationMin) {
        super(DIFF_CALLBACK);
        this.workoutDate = workoutDate;
        this.workoutDurationMin = workoutDurationMin;
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout de ítem correspondiente.
     *
     * @param parent   Contenedor padre al que se añadirá la vista.
     * @param viewType Tipo de vista (no se utiliza aquí, siempre es el mismo layout).
     * @return Nueva instancia de {@link ViewHolder}.
     */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula los datos de un {@link EntrenamientoEjercicioDTO} al ViewHolder,
     * incluyendo imagen, nombre y series en forma de chips.
     *
     * @param holder   ViewHolder que contiene las vistas del ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), workoutDate, workoutDurationMin);
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada elemento de ejercicio
     * dentro de un entrenamiento. Se encarga de cargar imagen, nombre y series.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumb;
        private final TextView tvName, tvSeriesCount;
        private final ChipGroup chipGroup;

        /**
         * Constructor que enlaza las vistas del layout item_workout_exercise.xml.
         *
         * @param v Vista inflada correspondiente a un ítem.
         */
        ViewHolder(@NonNull View v) {
            super(v);
            ivThumb       = v.findViewById(R.id.ivExerciseThumb);
            tvName        = v.findViewById(R.id.tvExerciseName);
            chipGroup     = v.findViewById(R.id.chipGroupSeries);
            tvSeriesCount = v.findViewById(R.id.tvSeriesCount);
        }

        /**
         * Enlaza los datos de {@link EntrenamientoEjercicioDTO} con las vistas:
         * <ul>
         *     <li>Nombre del ejercicio en un TextView.</li>
         *     <li>Imagen cargada con Glide (o placeholder si no hay URL).</li>
         *     <li>Chips que representan cada serie (repeticiones × peso).</li>
         *     <li>Texto que indica el número total de series.</li>
         * </ul>
         *
         * @param dto               Objeto con datos de la relación ejercicio–serie.
         * @param workoutDate       Fecha del entrenamiento (para referencia adicional si se desea usar).
         * @param workoutDurationMin Duración en minutos del entrenamiento (para referencia adicional si se desea usar).
         */
        void bind(EntrenamientoEjercicioDTO dto,
                  String workoutDate,
                  int workoutDurationMin) {
            tvName.setText(dto.getEjercicio().getNombre());

            // Carga de imagen con Glide y manejo de placeholder
            String url = dto.getEjercicio().getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivThumb.getContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(R.drawable.placeholder);
            }

            // Generación de chips para cada serie
            chipGroup.removeAllViews();
            for (SerieDTO s : dto.getSeries()) {
                Chip c = new Chip(chipGroup.getContext());
                c.setText(s.getRepeticiones() + "×" + s.getPeso() + "kg");
                c.setClickable(false);
                chipGroup.addView(c);
            }

            // Muestra la cantidad total de series
            int count = dto.getSeries().size();
            String sufijo = itemView.getContext().getString(R.string.series_count);
            tvSeriesCount.setText(count + " " + sufijo);
        }
    }

    /**
     * Callback utilizado por DiffUtil para determinar si dos instancias de
     * {@link EntrenamientoEjercicioDTO} representan el mismo elemento y si su contenido cambió.
     */
    private static final DiffUtil.ItemCallback<EntrenamientoEjercicioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull EntrenamientoEjercicioDTO a,
                                               @NonNull EntrenamientoEjercicioDTO b) {
                    return Objects.equals(a.getId(), b.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull EntrenamientoEjercicioDTO a,
                                                  @NonNull EntrenamientoEjercicioDTO b) {
                    return a.equals(b);
                }
            };
}
