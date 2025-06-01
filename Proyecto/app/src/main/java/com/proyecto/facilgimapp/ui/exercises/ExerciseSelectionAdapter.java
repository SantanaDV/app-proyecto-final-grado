package com.proyecto.facilgimapp.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ExerciseSelectionAdapter
        extends RecyclerView.Adapter<ExerciseSelectionAdapter.VH> {

    private final List<EjercicioDTO> exercises = new ArrayList<>();
    private final List<Integer> selectedIds = new ArrayList<>();

    /** Permite inicializar el estado “marcado” cuando reaparece el fragment */
    public void setInitiallySelectedIds(List<Integer> ids) {
        selectedIds.clear();
        if (ids != null) selectedIds.addAll(ids);
        notifyDataSetChanged();
    }


    public void setExercises(List<EjercicioDTO> list) {
        exercises.clear();
        exercises.addAll(list);
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedExerciseIds() {
        return selectedIds;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_selection, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override public int getItemCount() {
        return exercises.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivExerciseImage;
        CheckBox cbSelect;

        VH(@NonNull View v) {
            super(v);
            tvName           = v.findViewById(R.id.tvExerciseName);
            ivExerciseImage  = v.findViewById(R.id.ivExerciseImage);
            cbSelect         = v.findViewById(R.id.cbSelect);
        }

        void bind(EjercicioDTO dto) {
            tvName.setText(dto.getNombre());
            Picasso.get()
                    .load(dto.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(ivExerciseImage);

            cbSelect.setOnCheckedChangeListener(null);
            boolean isChecked = selectedIds.contains(dto.getIdEjercicio());
            cbSelect.setChecked(isChecked);

            cbSelect.setOnCheckedChangeListener((button, checked) -> {
                int id = dto.getIdEjercicio();
                if (checked) {
                    if (!selectedIds.contains(id)) selectedIds.add(id);
                } else {
                    selectedIds.remove((Integer) id);
                }
            });
        }
    }
}
