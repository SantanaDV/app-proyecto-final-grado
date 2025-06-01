package com.proyecto.facilgimapp.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.ItemUserBinding;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;

public class UserAdapter extends ListAdapter<UsuarioDTO, UserAdapter.UserViewHolder> {

    public interface OnUserInteractionListener {
        void onEditUser(UsuarioDTO user);
        void onToggleAdmin(UsuarioDTO user, boolean makeAdmin);
        void onDeleteUser(UsuarioDTO user);
    }

    private final OnUserInteractionListener listener;

    public UserAdapter(OnUserInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<UsuarioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UsuarioDTO>() {
                @Override public boolean areItemsTheSame(
                        @NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.getIdUsuario().equals(o2.getIdUsuario());
                }
                @Override public boolean areContentsTheSame(
                        @NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.equals(o2);
                }
            };

    @NonNull @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        ItemUserBinding b = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int pos) {
        UsuarioDTO u = getItem(pos);
        holder.binding.tvUsername.setText(u.getUsername());
        holder.binding.tvEmail.setText(u.getCorreo());
        holder.binding.tvAdminStatus.setText(
                u.isAdmin() ? R.string.admin : R.string.user
        );

        holder.binding.btnEditUser.setOnClickListener(v ->
                listener.onEditUser(u)
        );

        // Toggle admin text dinámico
        String toggleText = u.isAdmin()
                ? holder.binding.getRoot().getContext().getString(R.string.action_remove_admin)
                : holder.binding.getRoot().getContext().getString(R.string.action_toggle_admin);
        holder.binding.btnToggleAdmin.setText(toggleText);
        holder.binding.btnToggleAdmin.setOnClickListener(v ->
                listener.onToggleAdmin(u, !u.isAdmin())
        );

        holder.binding.btnDeleteUser.setOnClickListener(v ->
                listener.onDeleteUser(u)
        );
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {
        final ItemUserBinding binding;
        UserViewHolder(ItemUserBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
