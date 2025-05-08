package com.proyecto.facilgimapp.ui.workout;

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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

import java.util.Objects;

public class RelationAdapter
        extends ListAdapter<EntrenamientoEjercicioDTO, RelationAdapter.ViewHolder> {

    private final String workoutDate;
    private final int workoutDurationMin;

    public RelationAdapter(String workoutDate, int workoutDurationMin) {
        super(DIFF_CALLBACK);
        this.workoutDate = workoutDate;
        this.workoutDurationMin = workoutDurationMin;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), workoutDate, workoutDurationMin);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumb;
        private final TextView tvName, tvSeriesCount;
        private final ChipGroup chipGroup;

        ViewHolder(@NonNull View v) {
            super(v);
            ivThumb           = v.findViewById(R.id.ivExerciseThumb);
            tvName            = v.findViewById(R.id.tvExerciseName);
            chipGroup         = v.findViewById(R.id.chipGroupSeries);
            tvSeriesCount     = v.findViewById(R.id.tvSeriesCount);
        }

        void bind(EntrenamientoEjercicioDTO dto,
                  String workoutDate,
                  int workoutDurationMin) {
            tvName.setText(dto.getEjercicio().getNombre());

            // Imagen con Glide + placeholder
            String url = dto.getEjercicio().getImagenUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(ivThumb.getContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(R.drawable.placeholder);
            }

            // Chips de series…
            chipGroup.removeAllViews();
            for (SerieDTO s : dto.getSeries()) {
                Chip c = new Chip(chipGroup.getContext());
                c.setText(s.getRepeticiones() + "×" + s.getPeso() + "kg");
                c.setClickable(false);
                chipGroup.addView(c);
            }

            tvSeriesCount.setText(dto.getSeries().size() + " series");
        }
    }

    private static final DiffUtil.ItemCallback<EntrenamientoEjercicioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull EntrenamientoEjercicioDTO a,
                                               @NonNull EntrenamientoEjercicioDTO b) {
                    return Objects.equals(a.getId(), b.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull EntrenamientoEjercicioDTO a,
                                                  @NonNull EntrenamientoEjercicioDTO b) {
                    return a.equals(b);
                }
            };
}
