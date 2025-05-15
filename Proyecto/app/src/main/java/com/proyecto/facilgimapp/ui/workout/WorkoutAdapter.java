package com.proyecto.facilgimapp.ui.workout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.H> {

    private final List<EntrenamientoDTO> datos = new ArrayList<>();
    private final List<EntrenamientoDTO> fullList = new ArrayList<>();
    private final Consumer<Integer> onClick;

    public WorkoutAdapter(List<EntrenamientoDTO> d, Consumer<Integer> click) {
        if (d != null) {
            datos.addAll(d);
            fullList.addAll(d);
        }
        onClick = click;
    }

    public void updateList(List<EntrenamientoDTO> list) {
        datos.clear();
        fullList.clear();
        if (list != null) {
            datos.addAll(list);
            fullList.addAll(list);
        }
        notifyDataSetChanged();
    }

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

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new H(v);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int pos) {
        EntrenamientoDTO e = datos.get(pos);
        Integer idEntrenamiento = e.getId();

        if (idEntrenamiento != null) {
            holder.tv.setText(e.getNombre());
            holder.itemView.setOnClickListener(v -> {
                Log.d("DEBUG", "ID del entrenamiento: " + idEntrenamiento);
                onClick.accept(idEntrenamiento);
            });
        } else {
            Log.e("DEBUG", "ID del entrenamiento es nulo para: " + e.getNombre());
            holder.tv.setText("Entrenamiento no disponible");
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class H extends RecyclerView.ViewHolder {
        TextView tv;

        H(View v) {
            super(v);
            tv = v.findViewById(R.id.tvWorkoutName);
        }
    }
}
