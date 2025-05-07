package com.proyecto.facilgimapp.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.Entrenamiento;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.H> {
    private List<Entrenamiento> datos;
    private java.util.function.Consumer<Integer> onClick;

    public WorkoutAdapter(List<Entrenamiento> d, java.util.function.Consumer<Integer> click) {
        datos = d;
        onClick = click;
    }
    @NonNull @Override public H onCreateViewHolder(@NonNull ViewGroup p, int i) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_workout, p, false);
        return new H(v);
    }
    @Override public void onBindViewHolder(@NonNull H h, int pos) {
        Entrenamiento e = datos.get(pos);
        h.tv.setText(e.getNombre());
        h.itemView.setOnClickListener(v -> onClick.accept(e.getId()));
    }
    @Override public int getItemCount(){ return datos.size(); }
    static class H extends RecyclerView.ViewHolder {
        TextView tv;
        H(View v){ super(v); tv = v.findViewById(R.id.tvWorkoutName);}
    }
}
