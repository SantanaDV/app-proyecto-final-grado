package com.proyecto.facilgimapp.ui.workout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para editar las series de cada ejercicio dentro de un entrenamiento
 * en el diálogo de edición.
 * <p>
 * Cada {@link EntrenamientoEjercicioDTO} contiene una lista de {@link SerieDTO}.
 * Este adapter crea dinámicamente vistas de serie (repeticiones, peso, checkbox)
 * y sincroniza cualquier cambio de texto o checkbox con el objeto {@link SerieDTO}.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class EditExercisesAdapter
        extends RecyclerView.Adapter<EditExercisesAdapter.ViewHolder> {

    private final List<EntrenamientoEjercicioDTO> datosRelaciones;

    /**
     * Constructor que recibe la lista inicial de relaciones ejercicio–serie.
     *
     * @param inicial lista de {@link EntrenamientoEjercicioDTO} con series precargadas.
     */
    public EditExercisesAdapter(List<EntrenamientoEjercicioDTO> inicial) {
        datosRelaciones = new ArrayList<>();
        if (inicial != null) {
            datosRelaciones.addAll(inicial);
        }
    }

    /**
     * Devuelve el estado “actual” de todas las relaciones + series que el usuario ha modificado.
     *
     * @return lista de {@link EntrenamientoEjercicioDTO} con sus listas de {@link SerieDTO} actualizadas.
     */
    public List<EntrenamientoEjercicioDTO> getCurrentRelations() {
        return datosRelaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ev = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_ejercicio, parent, false);
        return new ViewHolder(ev);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrenamientoEjercicioDTO rel = datosRelaciones.get(position);
        holder.bind(rel);
    }

    @Override
    public int getItemCount() {
        return datosRelaciones.size();
    }

    /**
     * ViewHolder que contiene el nombre del ejercicio y un contenedor para las series.
     * Crea las vistas de cada serie y sincroniza eventos de texto y checkbox con el {@link SerieDTO}.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExerciseName;
        private final LinearLayout llSeriesContainer;

        ViewHolder(@NonNull View v) {
            super(v);
            tvExerciseName    = v.findViewById(R.id.tvExerciseName);
            llSeriesContainer = v.findViewById(R.id.llSeriesContainer);
        }

        /**
         * Vincula un {@link EntrenamientoEjercicioDTO} a esta celda:
         * <ul>
         *     <li>Establece el nombre en tvExerciseName.</li>
         *     <li>Genera un sub-item para cada {@link SerieDTO}, con EditText y CheckBox.</li>
         *     <li>Configura listeners de texto/checkbox para actualizar el {@link SerieDTO} correspondiente.</li>
         * </ul>
         *
         * @param rel relación {@link EntrenamientoEjercicioDTO} que contiene su lista de {@link SerieDTO}.
         */
        void bind(EntrenamientoEjercicioDTO rel) {
            tvExerciseName.setText(rel.getEjercicio().getNombre());
            llSeriesContainer.removeAllViews();

            // Si no hay series, creamos una serie inicial en blanco
            if (rel.getSeries() == null || rel.getSeries().isEmpty()) {
                List<SerieDTO> nuevas = new ArrayList<>();
                nuevas.add(new SerieDTO());
                rel.setSeries(nuevas);
            }

            // Para cada SerieDTO existente, generamos su vista
            for (SerieDTO serie : rel.getSeries()) {
                addOrRecreateSerieView(rel, serie);
            }
        }

        /**
         * Crea la vista de una {@link SerieDTO} e instala:
         * <ul>
         *   <li>EditText para repeticiones (etRepsSerie)</li>
         *   <li>EditText para peso (etWeightSerie)</li>
         *   <li>CheckBox para “completada” (cbDoneSerie)</li>
         *   <li>TextWatcher que actualiza repeticiones/peso en el {@link SerieDTO} y auto-checkea.</li>
         * </ul>
         *
         * @param rel   relación {@link EntrenamientoEjercicioDTO} a la que pertenece la serie.
         * @param serie {@link SerieDTO} con datos (posiblemente en blanco).
         */
        private void addOrRecreateSerieView(EntrenamientoEjercicioDTO rel, SerieDTO serie) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone   = serieView.findViewById(R.id.cbDoneSerie);

            // Precargar valores si existen
            if (serie.getRepeticiones() != null && serie.getRepeticiones() > 0) {
                etReps.setText(String.valueOf(serie.getRepeticiones()));
            }
            if (serie.getPeso() != null && serie.getPeso() > 0) {
                etWeight.setText(String.valueOf(serie.getPeso()));
            }
            cbDone.setChecked(serie.isCompletada());

            // Listener del CheckBox que actualizará SerieDTO
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> serie.setCompletada(isChecked));

            // TextWatcher para manejar cambios de texto en repeticiones/peso
            TextWatcher autoCheckWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    // Actualiza repeticiones
                    String repsStr = etReps.getText().toString().trim();
                    int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                    serie.setRepeticiones(reps);

                    // Actualiza peso
                    String wStr = etWeight.getText().toString().trim();
                    double peso = wStr.isEmpty() ? 0.0 : Double.parseDouble(wStr);
                    serie.setPeso(peso);

                    // Si ambos tienen datos válidos, marca el checkbox
                    boolean hasReps   = reps > 0;
                    boolean hasWeight = peso > 0;
                    if (hasReps && hasWeight && !cbDone.isChecked()) {
                        cbDone.setChecked(true);
                    }
                }
            };

            etReps.addTextChangedListener(autoCheckWatcher);
            etWeight.addTextChangedListener(autoCheckWatcher);

            // Long click para eliminar la serie, dejando por lo menos una
            serieView.setOnLongClickListener(v -> {
                List<SerieDTO> lista = rel.getSeries();
                if (lista.size() > 1) {
                    llSeriesContainer.removeView(serieView);
                    lista.remove(serie);
                } else {
                    Toast.makeText(itemView.getContext(),
                            R.string.debe_quedar_una_serie,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            llSeriesContainer.addView(serieView);
        }
    }
}
