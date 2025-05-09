package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;

import java.util.List;
import java.util.function.Consumer;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.H> {
    private List<EntrenamientoDTO> datos;
    private java.util.function.Consumer<Integer> onClick;

    public WorkoutAdapter(List<EntrenamientoDTO> d, Consumer<Integer> click) {
        datos = d;
        onClick = click;
    }
    @NonNull @Override public H onCreateViewHolder(@NonNull ViewGroup p, int i) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_workout, p, false);
        return new H(v);
    }
    @Override
    public void onBindViewHolder(@NonNull H h, int pos) {
        EntrenamientoDTO e = datos.get(pos);

        // Verificar si el id es nulo antes de continuar
        Integer idEntrenamiento = e.getId();
        if (idEntrenamiento != null) {
            h.tv.setText(e.getNombre());
            h.itemView.setOnClickListener(v -> {
                // Log para verificar que el id no es nulo
                Log.d("DEBUG", "ID del entrenamiento: " + idEntrenamiento);
                Bundle args = new Bundle();
                args.putInt("workoutId", idEntrenamiento.intValue());
                Navigation.findNavController(v).navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, args);
            });
        } else {
            // Si el id es nulo, loguear y manejar el error
            Log.e("DEBUG", "ID del entrenamiento es nulo para: " + e.getNombre());
            h.tv.setText("Entrenamiento no disponible");
        }
    }
    @Override public int getItemCount(){ return datos.size(); }
    static class H extends RecyclerView.ViewHolder {
        TextView tv;
        H(View v){ super(v); tv = v.findViewById(R.id.tvWorkoutName);}
    }
}
