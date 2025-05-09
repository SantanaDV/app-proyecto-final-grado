package com.proyecto.facilgimapp.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutSessionAdapter extends RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutViewHolder> {
    private final EntrenamientoDTO workoutDTO;
    private final List<Integer> exerciseIds;
    private final List<SerieDTO> allSeries = new ArrayList<>();
    public WorkoutSessionAdapter(EntrenamientoDTO workoutDTO, List<Integer> exerciseIds) {
        this.workoutDTO = workoutDTO;
        this.exerciseIds = exerciseIds;
    }

    @NonNull @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_session, parent, false);
        return new WorkoutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        int exerciseId = exerciseIds.get(position);
        holder.bind(exerciseId);
    }

    @Override
    public int getItemCount() {
        return exerciseIds.size();
    }

    public boolean allCompleted() {
        for (SerieDTO serie : allSeries) {
            if (!serie.isCompletada()) {
                return false;
            }
        }
        return true;
    }
    public List<SerieDTO> getSeries() {
        return allSeries;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExerciseName;
        private final EditText etReps;
        private final EditText etWeight;
        private final CheckBox checkBox;

        WorkoutViewHolder(View v) {
            super(v);
            tvExerciseName = v.findViewById(R.id.tvExerciseName);
            etReps = v.findViewById(R.id.etReps);
            etWeight = v.findViewById(R.id.etWeight);
            checkBox = v.findViewById(R.id.cbDone);
        }

        void bind(int exerciseId) {
            tvExerciseName.setText("Ejercicio #" + exerciseId);
            checkBox.setOnClickListener(v -> {
                boolean isChecked = checkBox.isChecked();
                SerieDTO serie = new SerieDTO(exerciseId, Integer.parseInt(etReps.getText().toString()),
                        Double.parseDouble(etWeight.getText().toString()));
                serie.setCompletada(isChecked);
                updateSeries(exerciseId, serie);
            });
        }





        private void updateSeries(int exerciseId, SerieDTO serie) {
            allSeries.add(serie);  // Aseguramos que las series se agreguen a la lista de series
        }
    }
}
