package com.proyecto.facilgimapp.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;

public class EjercicioDTOAdapter
        extends ListAdapter<EjercicioDTO, EjercicioDTOAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(int ejercicioId);
    }

    private final OnItemClick listener;

    public EjercicioDTOAdapter(OnItemClick listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioDTO dto = getItem(position);
        holder.bind(dto, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imgExercise);
            tvName  = itemView.findViewById(R.id.tvExerciseName);
        }

        void bind(EjercicioDTO dto, OnItemClick listener) {
            tvName.setText(dto.getNombre());

            String url = dto.getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivImage.getContext())
                        .load(url)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder);
            }

            itemView.setOnClickListener(v -> listener.onClick(dto.getIdEjercicio()));
        }
    }

    private static final DiffUtil.ItemCallback<EjercicioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<EjercicioDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull EjercicioDTO a, @NonNull EjercicioDTO b) {
                    return a.getIdEjercicio().equals(b.getIdEjercicio());
                }
                @Override
                public boolean areContentsTheSame(@NonNull EjercicioDTO a, @NonNull EjercicioDTO b) {
                    return a.equals(b);
                }
            };
}
