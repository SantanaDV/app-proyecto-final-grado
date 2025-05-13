package com.proyecto.facilgimapp.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.*;

import java.util.*;

public class WorkoutSessionAdapter extends RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutViewHolder> {

    private final EntrenamientoDTO workoutDTO;
    private final List<EjercicioDTO> exerciseList;

    // Mapa ejercicio -> lista de series
    private final Map<EjercicioDTO, List<SerieDTO>> seriesMap = new HashMap<>();

    public WorkoutSessionAdapter(EntrenamientoDTO workoutDTO, List<EjercicioDTO> exerciseList) {
        this.workoutDTO = workoutDTO;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_session, parent, false);
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

    public boolean allCompleted() {
        for (List<SerieDTO> series : seriesMap.values()) {
            for (SerieDTO s : series) {
                if (!s.isCompletada()) return false;
            }
        }
        return true;
    }

    public Map<EjercicioDTO, List<SerieDTO>> getSeriesMap() {
        return seriesMap;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExerciseName;
        private final ImageView ivExerciseImage;
        private final LinearLayout llSeriesContainer;
        private final Button btnAddSerie;

        WorkoutViewHolder(View v) {
            super(v);
            tvExerciseName = v.findViewById(R.id.tvExerciseName);
            ivExerciseImage = v.findViewById(R.id.ivExerciseImage);
            llSeriesContainer = v.findViewById(R.id.llSeriesContainer);
            btnAddSerie = v.findViewById(R.id.btnAddSerie);
        }

        void bind(EjercicioDTO ejercicio) {
            tvExerciseName.setText(ejercicio.getNombre());
            Glide.with(itemView.getContext())
                    .load(ejercicio.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(ivExerciseImage);

            // Asegurar que haya una lista inicial
            seriesMap.putIfAbsent(ejercicio, new ArrayList<>());

            // Añadir primera serie
            addSerieView(ejercicio);

            btnAddSerie.setOnClickListener(v -> addSerieView(ejercicio));
        }

        private void addSerieView(EjercicioDTO ejercicio) {
            View serieView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_serie, llSeriesContainer, false);

            EditText etReps = serieView.findViewById(R.id.etRepsSerie);
            EditText etWeight = serieView.findViewById(R.id.etWeightSerie);
            CheckBox cbDone = serieView.findViewById(R.id.cbDoneSerie);

            SerieDTO serie = new SerieDTO();

            cbDone.setOnCheckedChangeListener((btn, isChecked) -> {
                try {
                    int reps = Integer.parseInt(etReps.getText().toString());
                    double weight = Double.parseDouble(etWeight.getText().toString());

                    serie.setRepeticiones(reps);
                    serie.setPeso(weight);
                    serie.setCompletada(isChecked);

                    if (!seriesMap.get(ejercicio).contains(serie)) {
                        seriesMap.get(ejercicio).add(serie);
                    }

                } catch (NumberFormatException ignored) {}
            });

            // Eliminar serie con long click
            serieView.setOnLongClickListener(view -> {
                llSeriesContainer.removeView(serieView);
                seriesMap.get(ejercicio).remove(serie);
                return true;
            });

            llSeriesContainer.addView(serieView);
        }
    }
}
