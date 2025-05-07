package com.proyecto.facilgimapp.ui.series;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.databinding.ItemSeriesBinding;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

public class SeriesAdapter extends ListAdapter<SerieDTO, SeriesAdapter.SeriesViewHolder> {

    /** Listener que ahora usa SerieDTO, no la entidad Serie */
    public interface OnSeriesInteractionListener {
        void onDeleteSeries(SerieDTO serie);
    }

    private final OnSeriesInteractionListener listener;

    public SeriesAdapter(OnSeriesInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<SerieDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SerieDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull SerieDTO oldItem, @NonNull SerieDTO newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull SerieDTO oldItem, @NonNull SerieDTO newItem) {
                    return oldItem.getNumeroSerie().equals(newItem.getNumeroSerie())
                            && oldItem.getRepeticiones().equals(newItem.getRepeticiones())
                            && Double.compare(oldItem.getPeso(), newItem.getPeso()) == 0;
                }
            };

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeriesBinding binding = ItemSeriesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SeriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        SerieDTO serie = getItem(position);
        holder.binding.tvSeriesReps.setText("Reps: " + serie.getRepeticiones());
        holder.binding.tvSeriesWeight.setText("Peso: " + serie.getPeso() + " kg");
        holder.binding.getRoot().setOnLongClickListener(v -> {
            listener.onDeleteSeries(serie);
            return true;
        });
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        final ItemSeriesBinding binding;
        SeriesViewHolder(ItemSeriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
