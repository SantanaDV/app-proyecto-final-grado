package com.proyecto.facilgimapp.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.proyecto.facilgimapp.databinding.ItemUserBinding;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;

public class UserAdapter extends ListAdapter<UsuarioDTO, UserAdapter.UserViewHolder> {

    public interface OnUserInteractionListener {
        void onDeleteUser(UsuarioDTO user);
    }

    private final OnUserInteractionListener listener;

    public UserAdapter(OnUserInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<UsuarioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UsuarioDTO>() {
                @Override public boolean areItemsTheSame(@NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.getIdUsuario().equals(o2.getIdUsuario());
                }
                @Override public boolean areContentsTheSame(@NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.getUsername().equals(o2.getUsername())
                            && o1.getCorreo().equals(o2.getCorreo());
                }
            };

    @NonNull @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding b = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int pos) {
        UsuarioDTO u = getItem(pos);
        holder.binding.tvUsername.setText(u.getUsername());
        holder.binding.tvEmail.setText(u.getCorreo());
        holder.binding.getRoot().setOnLongClickListener(v -> {
            listener.onDeleteUser(u);
            return true;
        });
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final ItemUserBinding binding;
        UserViewHolder(ItemUserBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
