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

/**
 * Adaptador que gestiona la sesión de entrenamiento en curso mostrando cada ejercicio
 * y permitiendo al usuario añadir, completar o eliminar series (repeticiones × peso).
 * <p>
 * Mantiene un mapa interno de {@link SerieDTO} para cada {@link EjercicioDTO}
 * y ofrece métodos para verificar si todas las series están completadas y para
 * construir la lista final de {@link EntrenamientoEjercicioDTO} que envía el ViewModel.
 * </p>
 * 
 * @author Francisco Santana
 */
public class WorkoutSessionAdapter extends RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutViewHolder> {

    /**
     * DTO del entrenamiento que contiene datos generales como nombre, fecha, tipo, etc.
     */
    private final EntrenamientoDTO workoutDTO;

    /**
     * Lista de ejercicios que componen la sesión de entrenamiento.
     */
    private final List<EjercicioDTO> exerciseList;

    /**
     * Mapa que asocia cada {@link EjercicioDTO} con su lista de {@link SerieDTO}.
     */
    private final Map<EjercicioDTO, List<SerieDTO>> seriesMap = new HashMap<>();

    /**
     * Constructor que inicializa el adaptador con el DTO del entrenamiento y la lista de ejercicios.
     *
     * @param workoutDTO   DTO que contiene la información del entrenamiento.
     * @param exerciseList Lista de ejercicios a mostrar en la sesión.
     */
    public WorkoutSessionAdapter(EntrenamientoDTO workoutDTO, List<EjercicioDTO> exerciseList) {
        this.workoutDTO = workoutDTO;
        this.exerciseList = exerciseList;
    }

    /**
     * Crea un nuevo {@link WorkoutViewHolder} inflando el layout de cada item de ejercicio en sesión.
     *
     * @param parent   Contenedor padre donde se inflará la vista.
     * @param viewType Tipo de vista (no se utiliza, siempre es R.layout.item_workout_session).
     * @return Instancia de {@link WorkoutViewHolder}.
     */
    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_session, parent, false);
        return new WorkoutViewHolder(v);
    }

    /**
     * Vincula los datos del ejercicio en la posición dada al {@link WorkoutViewHolder},
     * iniciando o recreando la lista de series asociada en caso necesario.
     *
     * @param holder   ViewHolder que contiene las vistas del item.
     * @param position Posición del elemento en {@code exerciseList}.
     */
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        EjercicioDTO ejercicio = exerciseList.get(position);
        holder.bind(ejercicio);
    }

    /**
     * Retorna el número total de ejercicios en la sesión.
     *
     * @return Tamaño de {@code exerciseList}.
     */
    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * Verifica si todas las series de todos los ejercicios están completadas.
     * <p>
     * Retorna {@code true} solo si cada {@link SerieDTO} está marcada como completada
     * y tanto repeticiones como peso son mayores que cero. Trata nulos como cero.
     * </p>
     *
     * @return {@code true} si todas las series cumplen la condición, {@code false} en caso contrario.
     */
    public boolean allCompleted() {
        for (List<SerieDTO> series : seriesMap.values()) {
            for (SerieDTO s : series) {
                Integer repsObj = s.getRepeticiones();
                int reps = (repsObj == null ? 0 : repsObj);
                Double pesoObj = s.getPeso();
                double peso = (pesoObj == null ? 0.0 : pesoObj);

                if (!s.isCompletada() || reps <= 0 || peso <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Retorna el mapa interno de ejercicios a lista de series, para que el ViewModel
     * pueda recogerlo al guardar la sesión.
     *
     * @return Mapa de {@link EjercicioDTO} a {@code List<SerieDTO>}.
     */
    public Map<EjercicioDTO, List<SerieDTO>> getSeriesMap() {
        return seriesMap;
    }

    /**
     * ViewHolder que representa cada ejercicio dentro de la sesión de entrenamiento,
     * mostrando su nombre, imagen, lista dinámica de series y opciones de menú.
     */
    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExerciseName;
        private final ImageView ivExerciseImage;
        private final ImageView ivMoreOptions;
        private final LinearLayout llSeriesContainer;
        private final Button btnAddSerie;

        /**
         * Constructor que enlaza las vistas del layout {@code item_workout_session.xml}.
         *
         * @param v Vista inflada de cada item.
         */
        public WorkoutViewHolder(@NonNull View v) {
            super(v);
            tvExerciseName    = v.findViewById(R.id.tvExerciseName);
            ivExerciseImage   = v.findViewById(R.id.ivExerciseImage);
            ivMoreOptions     = v.findViewById(R.id.ivMoreOptions);
            llSeriesContainer = v.findViewById(R.id.llSeriesContainer);
            btnAddSerie       = v.findViewById(R.id.btnAddSerie);
        }

        /**
         * Vincula los datos de un {@link EjercicioDTO} al ViewHolder:
         * <ul>
         *   <li>Carga el nombre e imagen del ejercicio.</li>
         *   <li>Inicializa la lista de series en {@code seriesMap} si no existe.</li>
         *   <li>Reconstruye o crea la vista de cada serie mediante {@link #addExistingSerieView}
         *       o {@link #addSerieView} según el caso.</li>
         *   <li>Configura el botón para añadir una nueva serie.</li>
         *   <li>Configura el menú de opciones (eliminar una serie o completar todas).</li>
         * </ul>
         *
         * @param ejercicio Ejercicio cuyos datos se mostrarán.
         */
        public void bind(EjercicioDTO ejercicio) {
            // Carga de datos básicos: nombre e imagen
            tvExerciseName.setText(ejercicio.getNombre());
            Glide.with(itemView.getContext())
                    .load(ejercicio.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(ivExerciseImage);

            // Garantiza la inicialización de la lista de series para este ejercicio
            seriesMap.putIfAbsent(ejercicio, new ArrayList<>());

            // Limpia vistas anteriores para evitar duplicados
            llSeriesContainer.removeAllViews();

            // Si no hay series existentes, crea una por defecto
            if (seriesMap.get(ejercicio).isEmpty()) {
                addSerieView(ejercicio);
            } else {
                // Recrea la vista para cada SerieDTO existente
                for (SerieDTO serie : seriesMap.get(ejercicio)) {
                    addExistingSerieView(ejercicio, serie);
                }
            }

            // Listener para añadir una nueva serie en blanco
            btnAddSerie.setOnClickListener(v -> addSerieView(ejercicio));

            // Icono de opciones con PopupMenu
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
         * Crea una nueva vista de serie en blanco para el ejercicio dado,
         * la añade a {@code llSeriesContainer} y agrega el {@link SerieDTO} al mapa.
         * <p>
         * Configura:
         * <ul>
         *   <li>Listeners para el {@link CheckBox} de completado.</li>
         *   <li>TextWatcher para actualizar repeticiones y peso, y auto-marcar el checkbox.</li>
         *   <li>Listener de pulsación prolongada para eliminar la serie si quedan ≥1.</li>
         * </ul>
         *
         * @param ejercicio Ejercicio al que pertenece la serie.
         */
        private void addSerieView(EjercicioDTO ejercicio) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone   = serieView.findViewById(R.id.cbDoneSerie);

            // Crea el DTO en blanco y lo añade al mapa
            SerieDTO serie = new SerieDTO();
            seriesMap.get(ejercicio).add(serie);

            // Listener del checkbox para marcar completada la serie
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) ->
                    serie.setCompletada(isChecked)
            );

            // TextWatcher para repeticiones y peso que actualiza el DTO y auto-check
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

            // Long click para eliminar la serie, dejando al menos una
            serieView.setOnLongClickListener(v -> {
                List<SerieDTO> lista = seriesMap.get(ejercicio);
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

        /**
         * Reconstruye la vista de una {@link SerieDTO} existente al reciclar el ViewHolder.
         * <p>
         * Precarga los valores en los {@link EditText} y {@link CheckBox}, y configura
         * los mismos listeners que en {@link #addSerieView} para actualización y eliminación.
         *
         * @param ejercicio Ejercicio al que pertenece la serie.
         * @param serie     DTO con datos previos de la serie.
         */
        private void addExistingSerieView(EjercicioDTO ejercicio, SerieDTO serie) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps   = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone   = serieView.findViewById(R.id.cbDoneSerie);

            // Precarga los valores en la vista
            etReps.setText(String.valueOf(serie.getRepeticiones()));
            etWeight.setText(String.valueOf(serie.getPeso()));
            cbDone.setChecked(serie.isCompletada());

            // Listeners iguales a addSerieView
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) ->
                    serie.setCompletada(isChecked)
            );

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

            // Long click para eliminar la serie, dejando al menos una
            serieView.setOnLongClickListener(v -> {
                List<SerieDTO> lista = seriesMap.get(ejercicio);
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

        /**
         * Elimina la última serie del ejercicio dado, siempre que quede al menos una.
         * <p>
         * Si no hay series o solo queda una, muestra un Toast informativo.
         * </p>
         *
         * @param ejercicio Ejercicio cuyas series se desean modificar.
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
            int lastIndex = llSeriesContainer.getChildCount() - 1;
            View ultimaSerieView = llSeriesContainer.getChildAt(lastIndex);
            llSeriesContainer.removeView(ultimaSerieView);
            listaSeries.remove(listaSeries.size() - 1);
        }

        /**
         * Marca todas las series como completadas para el ejercicio dado,
         * siempre que cada serie tenga repeticiones y peso no vacío.
         * <p>
         * Si alguna serie no cumple, muestra un Toast y no realiza cambios.
         * </p>
         *
         * @param ejercicio Ejercicio cuyas series se desean completar.
         */
        private void completarTodasSeries(EjercicioDTO ejercicio) {
            List<SerieDTO> listaSeries = seriesMap.get(ejercicio);
            if (listaSeries == null || listaSeries.isEmpty()) {
                Toast.makeText(itemView.getContext(),
                        R.string.no_hay_series_para_completar,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que repeticiones y peso estén completos en cada vista
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

            // Si todas están completas en contenido, marcarlas como completadas
            for (int i = 0; i < listaSeries.size(); i++) {
                View serieView = llSeriesContainer.getChildAt(i);
                CheckBox cbDone = serieView.findViewById(R.id.cbDoneSerie);
                cbDone.setChecked(true);
                listaSeries.get(i).setCompletada(true);
            }
        }
    }

    /**
     * Construye la lista de {@link EntrenamientoEjercicioDTO} a partir de los datos ingresados
     * en {@code seriesMap} y la lista de ejercicios.
     * <p>
     * Cada {@code EntrenamientoEjercicioDTO} contiene el ejercicio, su lista de series,
     * y un orden incremental a partir de 1. Se omiten ejercicios sin series.
     * </p>
     *
     * @return Lista de {@link EntrenamientoEjercicioDTO} lista para enviar al ViewModel.
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
