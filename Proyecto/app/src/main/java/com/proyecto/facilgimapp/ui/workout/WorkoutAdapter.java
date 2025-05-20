package com.proyecto.facilgimapp.ui.workout;

import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.PopupMenu;

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

    private final Consumer<EntrenamientoDTO> onItemClick;
    private final Consumer<EntrenamientoDTO> onViewDescription;
    private final Consumer<EntrenamientoDTO> onEdit;
    private final Consumer<EntrenamientoDTO> onDelete;

    public WorkoutAdapter(List<EntrenamientoDTO> d,
                          Consumer<EntrenamientoDTO> onClick,
                          Consumer<EntrenamientoDTO> onViewDescription,
                          Consumer<EntrenamientoDTO> onEdit,
                          Consumer<EntrenamientoDTO> onDelete) {
        this.onItemClick = onClick;
        this.onViewDescription = onViewDescription;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        if (d != null) {
            datos.addAll(d);
            fullList.addAll(d);
        }
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

            // Click en la tarjeta para ver detalle
            holder.itemView.setOnClickListener(v -> {
                Log.d("DEBUG", "ID del entrenamiento: " + idEntrenamiento);
                onItemClick.accept(e);
            });

            // Menú de opciones
            holder.btnOptions.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.menu_workout_item);

                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.action_view_description) {
                        onViewDescription.accept(e);
                        return true;
                    } else if (id == R.id.action_edit) {
                        onEdit.accept(e);
                        return true;
                    } else if (id == R.id.action_delete) {
                        onDelete.accept(e);
                        return true;
                    }

                    return false;
                });

                popup.show();
            });

        } else {
            Log.e("DEBUG", "ID del entrenamiento es nulo para: " + e.getNombre());
            holder.tv.setText(R.string.entrenamiento_no_disponible);
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class H extends RecyclerView.ViewHolder {
        TextView tv;
        ImageButton btnOptions;

        H(View v) {
            super(v);
            tv = v.findViewById(R.id.tvWorkoutName);
            btnOptions = v.findViewById(R.id.btnOptions);
        }
    }
}
