package com.proyecto.facilgimapp.ui.types;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.databinding.ItemTypeBinding;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;

public class TypeAdapter extends ListAdapter<TipoEntrenamientoDTO, TypeAdapter.TypeViewHolder> {
    public interface OnTypeInteractionListener {
        void onEdit(TipoEntrenamientoDTO type);
        void onDelete(TipoEntrenamientoDTO type);
    }

    private final OnTypeInteractionListener listener;

    public TypeAdapter(OnTypeInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<TipoEntrenamientoDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TipoEntrenamientoDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull TipoEntrenamientoDTO oldItem,
                                               @NonNull TipoEntrenamientoDTO newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull TipoEntrenamientoDTO oldItem,
                                                  @NonNull TipoEntrenamientoDTO newItem) {
                    return oldItem.getNombre().equals(newItem.getNombre());
                }
            };

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTypeBinding binding = ItemTypeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TypeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        TipoEntrenamientoDTO type = getItem(position);
        holder.binding.tvTypeName.setText(type.getNombre());
        holder.binding.getRoot().setOnClickListener(v ->
                listener.onEdit(type)
        );
        holder.binding.getRoot().setOnLongClickListener(v -> {
            listener.onDelete(type);
            return true;
        });
    }

    static class TypeViewHolder extends RecyclerView.ViewHolder {
        final ItemTypeBinding binding;
        TypeViewHolder(ItemTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
