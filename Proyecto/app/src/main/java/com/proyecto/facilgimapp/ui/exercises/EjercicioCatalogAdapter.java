package com.proyecto.facilgimapp.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EjercicioCatalogAdapter
        extends ListAdapter<EjercicioDTO, EjercicioCatalogAdapter.ViewHolder> {

    public interface OnLongItemClick {
        void onLongClick(EjercicioDTO ejercicio);
    }

    private final OnLongItemClick longClickListener;
    private final List<EjercicioDTO> fullList = new ArrayList<>();

    public EjercicioCatalogAdapter(OnLongItemClick longClickListener) {
        super(DIFF_CALLBACK);
        this.longClickListener = longClickListener;
        // Habilitamos el uso de IDs estables
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // Usamos el ID del ejercicio como stable ID
        return Objects.requireNonNull(getItem(position)).getIdEjercicio();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    // Variante normal
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), longClickListener);
    }

    // Variante con payloads
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // si no hay payloads, bind normal
            onBindViewHolder(holder, position);
        } else {
            // actualizaciones parciales
            EjercicioDTO dto = getItem(position);
            for (Object pl : payloads) {
                if (pl instanceof Bundle) {
                    Bundle b = (Bundle) pl;
                    if (b.containsKey("nombre")) {
                        holder.tvName.setText(b.getString("nombre"));
                    }
                    if (b.containsKey("imagenUrl")) {
                        String url = b.getString("imagenUrl");
                        if (url != null && !url.isEmpty()) {
                            Glide.with(holder.ivImage.getContext())
                                    .asDrawable()
                                    .load(url)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .centerCrop()
                                    .into(holder.ivImage);
                        } else {
                            holder.ivImage.setImageResource(R.drawable.placeholder);
                        }
                    }
                }
            }
        }
    }

    /**
     * Reemplaza la lista y, si es fullUpdate, refresca fullList
     */
    public void submitList(List<EjercicioDTO> list, boolean isFullUpdate) {
        super.submitList(list, () -> {
            if (isFullUpdate) {
                fullList.clear();
                fullList.addAll(list);
            }
        });
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            submitList(new ArrayList<>(fullList), false);
            return;
        }
        List<EjercicioDTO> filtered = new ArrayList<>();
        for (EjercicioDTO e : fullList) {
            if (e.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        submitList(filtered, false);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imgExercise);
            tvName  = itemView.findViewById(R.id.tvExerciseName);
        }

        void bind(EjercicioDTO dto, OnLongItemClick longClickListener) {
            tvName.setText(dto.getNombre());
            String url = dto.getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivImage.getContext())
                        .asDrawable()
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.placeholder);
            }

            if (SessionManager.getAuthorities(ivImage.getContext())
                    .contains("ROLE_ADMIN")) {
                itemView.setOnLongClickListener(v -> {
                    longClickListener.onLongClick(dto);
                    return true;
                });
            }
        }
    }

    private static final DiffUtil.ItemCallback<EjercicioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<EjercicioDTO>() {
                @Override public boolean areItemsTheSame(
                        @NonNull EjercicioDTO a,
                        @NonNull EjercicioDTO b) {
                    return Objects.equals(a.getIdEjercicio(), b.getIdEjercicio());
                }
                @Override public boolean areContentsTheSame(
                        @NonNull EjercicioDTO a,
                        @NonNull EjercicioDTO b) {
                    return Objects.equals(a.getNombre(),   b.getNombre())
                            && Objects.equals(a.getImagenUrl(), b.getImagenUrl());
                }
                @Override @Nullable
                public Object getChangePayload(
                        @NonNull EjercicioDTO oldItem,
                        @NonNull EjercicioDTO newItem) {
                    Bundle diff = new Bundle();
                    if (!Objects.equals(oldItem.getNombre(), newItem.getNombre())) {
                        diff.putString("nombre", newItem.getNombre());
                    }
                    if (!Objects.equals(oldItem.getImagenUrl(), newItem.getImagenUrl())) {
                        diff.putString("imagenUrl", newItem.getImagenUrl());
                    }
                    return diff.size() == 0 ? null : diff;
                }
            };
}
