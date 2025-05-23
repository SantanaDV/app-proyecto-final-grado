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
import com.proyecto.facilgimapp.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class EjercicioCatalogAdapter extends ListAdapter<EjercicioDTO, EjercicioCatalogAdapter.ViewHolder> {


    public interface OnLongItemClick {
        void onLongClick(EjercicioDTO ejercicio);
    }


    private final OnLongItemClick longClickListener;
    private final List<EjercicioDTO> fullList = new ArrayList<>();

    public EjercicioCatalogAdapter( OnLongItemClick longClickListener) {
        super(DIFF_CALLBACK);
        this.longClickListener = longClickListener;
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
        holder.bind(dto, longClickListener);
    }

    public void submitList(List<EjercicioDTO> list, boolean isFullUpdate) {
        super.submitList(list);
        if (isFullUpdate) {
            fullList.clear();
            if (list != null) fullList.addAll(list);
        }
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            submitList(new ArrayList<>(fullList));
            return;
        }

        List<EjercicioDTO> filtered = new ArrayList<>();
        for (EjercicioDTO e : fullList) {
            if (e.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        submitList(filtered, false); //No sobreescribimos la lista para poder recuperarla
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imgExercise);
            tvName = itemView.findViewById(R.id.tvExerciseName);
        }

        void bind(EjercicioDTO dto,  OnLongItemClick longClickListener) {
            tvName.setText(dto.getNombre());

            String url = dto.getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivImage.getContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder);
            }

            if (SessionManager.getAuthorities(ivImage.getContext()).contains("ROLE_ADMIN")) {
                itemView.setOnLongClickListener(v -> {
                    longClickListener.onLongClick(dto);
                    return true;
                });
            }
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
