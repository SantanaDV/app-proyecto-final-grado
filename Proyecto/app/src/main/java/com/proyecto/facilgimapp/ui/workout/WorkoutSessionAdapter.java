package com.proyecto.facilgimapp.ui.workout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.*;

import java.util.*;

public class WorkoutSessionAdapter extends RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutViewHolder> {

    private final EntrenamientoDTO workoutDTO;
    private final List<EjercicioDTO> exerciseList;

    /** Mapa ejercicio lista de SerieDTO **/
    private final Map<EjercicioDTO, List<SerieDTO>> seriesMap = new HashMap<>();

    public WorkoutSessionAdapter(EntrenamientoDTO workoutDTO, List<EjercicioDTO> exerciseList) {
        this.workoutDTO = workoutDTO;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_session, parent, false);
        return new WorkoutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        EjercicioDTO ejercicio = exerciseList.get(position);
        holder.bind(ejercicio);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * Devuelve true sólo si:
     *   Cada SerieDTO está marcada como completada.
     *   Cada SerieDTO tiene repeticiones > 0 Y peso > 0.
     */
    public boolean allCompleted() {
        for (List<SerieDTO> series : seriesMap.values()) {
            for (SerieDTO s : series) {
                //  Si getRepeticiones() es null, lo tratamos como 0
                Integer repsObj = s.getRepeticiones();
                int reps = (repsObj == null ? 0 : repsObj);

                // Si getPeso() es null, lo tratamos como 0
                Double pesoObj = s.getPeso();
                double peso = (pesoObj == null ? 0.0 : pesoObj);

                //  Sólo aceptamos como completos los que estén marcados Y tengan reps>0 Y peso>0
                if (!s.isCompletada() || reps <= 0 || peso <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Para que el ViewModel recoja el resultado al guardarlo **/
    public Map<EjercicioDTO, List<SerieDTO>> getSeriesMap() {
        return seriesMap;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExerciseName;
        private final ImageView ivExerciseImage;
        private final ImageView ivMoreOptions;
        private final LinearLayout llSeriesContainer;
        private final Button btnAddSerie;

        public WorkoutViewHolder(@NonNull View v) {
            super(v);
            tvExerciseName    = v.findViewById(R.id.tvExerciseName);
            ivExerciseImage   = v.findViewById(R.id.ivExerciseImage);
            ivMoreOptions     = v.findViewById(R.id.ivMoreOptions);
            llSeriesContainer = v.findViewById(R.id.llSeriesContainer);
            btnAddSerie       = v.findViewById(R.id.btnAddSerie);
        }

        public void bind(EjercicioDTO ejercicio) {
            // Carga de datos básicos
            tvExerciseName.setText(ejercicio.getNombre());
            Glide.with(itemView.getContext())
                    .load(ejercicio.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(ivExerciseImage);

            // Aseguramos la lista inicial para el ejercicio
            seriesMap.putIfAbsent(ejercicio, new ArrayList<>());

            // Limpiamos cualquier serie anterior (esto evita duplicados al reciclar ViewHolder)
            llSeriesContainer.removeAllViews();

            // Si no hay ninguna serie, agregamos una por defecto
            if (seriesMap.get(ejercicio).isEmpty()) {
                addSerieView(ejercicio);
            } else {
                // Si ya hay series en seriesMap
                // recreamos las vistas para cada SerieDTO:
                for (SerieDTO serie : seriesMap.get(ejercicio)) {
                    addExistingSerieView(ejercicio, serie);
                }
            }

            // Listener para Añadir serie
            btnAddSerie.setOnClickListener(v -> addSerieView(ejercicio));

            // Listener tres puntos PopupMenu
            ivMoreOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), ivMoreOptions);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_series_options, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.action_delete_one) {
                        eliminarUnaSerie(ejercicio);
                        return true;
                    } else if (id == R.id.action_complete_all) {
                        completarTodasSeries(ejercicio);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        /**
         * Crea una nueva vista de serie en blanco y la añade a llSeriesContainer y a seriesMap
         */
        private void addSerieView(EjercicioDTO ejercicio) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone   = serieView.findViewById(R.id.cbDoneSerie);

            // Creamos la SerieDTO en blanco y la añadimos al mapa
            SerieDTO serie = new SerieDTO();
            seriesMap.get(ejercicio).add(serie);

            // Listener del checkbox
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                serie.setCompletada(isChecked);
            });

            // TextWatcher compartido para repeticiones y peso, auto-check si ambos no están vacíos
            TextWatcher autoCheckWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String repsStr = etReps.getText().toString().trim();
                    int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                    serie.setRepeticiones(reps);

                    String wStr = etWeight.getText().toString().trim();
                    double peso = wStr.isEmpty() ? 0 : Double.parseDouble(wStr);
                    serie.setPeso(peso);

                    boolean hasReps   = repsStr.length() > 0;
                    boolean hasWeight = wStr.length() > 0;
                    if (hasReps && hasWeight && !cbDone.isChecked()) {
                        cbDone.setChecked(true);
                    }
                }
            };

            etReps.addTextChangedListener(autoCheckWatcher);
            etWeight.addTextChangedListener(autoCheckWatcher);

            // Long click sobre la vista completa de la serie para eliminarla
            serieView.setOnLongClickListener(v -> {
                if (seriesMap.get(ejercicio).size() > 1) {
                    llSeriesContainer.removeView(serieView);
                    seriesMap.get(ejercicio).remove(serie);
                } else {
                    Toast.makeText(itemView.getContext(),
                            R.string.debe_quedar_una_serie,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            llSeriesContainer.addView(serieView);
        }

        /**
         * Reconstruye la vista de una SerieDTO existente (al reciclar ViewHolder).
         * Copia sus valores en los EditText y el CheckBox, y le añade listeners nuevos.
         */
        private void addExistingSerieView(EjercicioDTO ejercicio, SerieDTO serie) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone   = serieView.findViewById(R.id.cbDoneSerie);

            // Rellenamos los campos con los datos de la SerieDTO
            etReps.setText( String.valueOf(serie.getRepeticiones()) );
            etWeight.setText( String.valueOf(serie.getPeso()) );
            cbDone.setChecked( serie.isCompletada() );

            // Listeners como en addSerieView
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                serie.setCompletada(isChecked);
            });

            TextWatcher autoCheckWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String repsStr = etReps.getText().toString().trim();
                    int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                    serie.setRepeticiones(reps);

                    String wStr = etWeight.getText().toString().trim();
                    double peso = wStr.isEmpty() ? 0 : Double.parseDouble(wStr);
                    serie.setPeso(peso);

                    boolean hasReps   = repsStr.length() > 0;
                    boolean hasWeight = wStr.length() > 0;
                    if (hasReps && hasWeight && !cbDone.isChecked()) {
                        cbDone.setChecked(true);
                    }
                }
            };

            etReps.addTextChangedListener(autoCheckWatcher);
            etWeight.addTextChangedListener(autoCheckWatcher);

            // Long click para eliminar (misma lógica)
            serieView.setOnLongClickListener(v -> {
                if (seriesMap.get(ejercicio).size() > 1) {
                    llSeriesContainer.removeView(serieView);
                    seriesMap.get(ejercicio).remove(serie);
                } else {
                    Toast.makeText(itemView.getContext(),
                            R.string.debe_quedar_una_serie,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            llSeriesContainer.addView(serieView);
        }

        /**
         * Opción “Eliminar una serie”.
         * Quita **la última** serie agregada (siempre debe quedar ≥1).
         */
        private void eliminarUnaSerie(EjercicioDTO ejercicio) {
            List<SerieDTO> listaSeries = seriesMap.get(ejercicio);
            if (listaSeries == null || listaSeries.isEmpty()) {
                Toast.makeText(itemView.getContext(),
                        R.string.no_hay_series_para_eliminar,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (listaSeries.size() <= 1) {
                Toast.makeText(itemView.getContext(),
                        R.string.debe_quedar_una_serie,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Eliminamos la vista y el DTO correspondiente
            int lastIndex = llSeriesContainer.getChildCount() - 1;
            View ultimaSerieView = llSeriesContainer.getChildAt(lastIndex);
            llSeriesContainer.removeView(ultimaSerieView);
            listaSeries.remove(listaSeries.size() - 1);
        }

        /**
         * Opción “Completar todas las series”.
         * Antes de marcar, comprueba que **todas** las series tengan repetición y el peso no este vacío.
         */
        private void completarTodasSeries(EjercicioDTO ejercicio) {
            List<SerieDTO> listaSeries = seriesMap.get(ejercicio);
            if (listaSeries == null || listaSeries.isEmpty()) {
                Toast.makeText(itemView.getContext(),
                        R.string.no_hay_series_para_completar,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Recorremos cada serie, buscamos su vista asociada para validar
            for (int i = 0; i < listaSeries.size(); i++) {
                View serieView = llSeriesContainer.getChildAt(i);
                EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
                EditText etWeight = serieView.findViewById(R.id.etWeightSerie);

                String repsStr = etReps.getText().toString().trim();
                String wStr    = etWeight.getText().toString().trim();

                if (repsStr.isEmpty() || wStr.isEmpty()) {
                    Toast.makeText(itemView.getContext(),
                            R.string.debe_completar_todas_las_series,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Si llegamos aquí, todos los campos están completos, entonces marcamos todas como completadas
            for (int i = 0; i < listaSeries.size(); i++) {
                View serieView = llSeriesContainer.getChildAt(i);
                CheckBox cbDone = serieView.findViewById(R.id.cbDoneSerie);
                cbDone.setChecked(true);
                // Actualizamos el DTO
                listaSeries.get(i).setCompletada(true);
            }
        }
    }

    /**
     * Construye la lista de EntrenamientoEjercicioDTO que luego envía el ViewModel
     */
    public List<EntrenamientoEjercicioDTO> getEntrenamientoEjercicioDTOList() {
        List<EntrenamientoEjercicioDTO> list = new ArrayList<>();
        int orden = 1;
        for (EjercicioDTO ejercicio : exerciseList) {
            List<SerieDTO> series = seriesMap.get(ejercicio);
            if (series == null || series.isEmpty()) continue;

            EntrenamientoEjercicioDTO rel = new EntrenamientoEjercicioDTO();
            rel.setEjercicio(ejercicio);
            rel.setSeries(series);
            rel.setOrden(orden++);
            list.add(rel);
        }
        return list;
    }
}
