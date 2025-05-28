package com.proyecto.facilgimapp.ui.exercises;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;

import java.util.ArrayList;
import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.VH> {

    private final List<EjercicioDTO> items = new ArrayList<>();

    public void submitList(List<EjercicioDTO> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
            EjercicioDTO e = items.get(position);
        holder.tvName.setText(e.getNombre());
        Glide.with(holder.img.getContext())
                .asDrawable()
                .load(e.getImagenUrl())
                .placeholder(R.drawable.placeholder)     // mientras carga
                .error(R.drawable.placeholder)           // si falla la descarga
                .fallback(R.drawable.placeholder)        // si la URL es null
                .centerCrop()
                .into(holder.img);



    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgExercise);
            tvName = itemView.findViewById(R.id.tvExerciseName);
        }
    }
}