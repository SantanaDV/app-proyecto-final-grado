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

/**
 * Adaptador para mostrar una lista de usuarios en un RecyclerView.
 * Utiliza ListAdapter con DiffUtil para gestionar eficientemente los cambios de datos.
 * Cada elemento permite editar, alternar privilegios de administrador y eliminar al usuario.
 * 
 * @author Francisco Santana
 */
public class UserAdapter extends ListAdapter<UsuarioDTO, UserAdapter.UserViewHolder> {

    /**
     * Interfaz para manejar las interacciones sobre cada usuario:
     * editar, alternar estado de administrador o eliminar.
     */
    public interface OnUserInteractionListener {
        /**
         * Se invoca cuando el usuario solicita editar un registro.
         * 
         * @param user Usuario seleccionado para editar.
         */
        void onEditUser(UsuarioDTO user);

        /**
         * Se invoca cuando el usuario solicita alternar el estado de administrador.
         * 
         * @param user      Usuario seleccionado.
         * @param makeAdmin {@code true} para otorgar permisos de administrador, {@code false} para revocarlos.
         */
        void onToggleAdmin(UsuarioDTO user, boolean makeAdmin);

        /**
         * Se invoca cuando el usuario solicita eliminar un registro.
         * 
         * @param user Usuario seleccionado para eliminar.
         */
        void onDeleteUser(UsuarioDTO user);
    }

    /**
     * Callback que compara dos instancias de UsuarioDTO para detectar cambios
     * y optimizar la actualización del RecyclerView.
     */
    private static final DiffUtil.ItemCallback<UsuarioDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UsuarioDTO>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.getIdUsuario().equals(o2.getIdUsuario());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull UsuarioDTO o1, @NonNull UsuarioDTO o2) {
                    return o1.equals(o2);
                }
            };

    /**
     * Listener que maneja acciones sobre cada fila de usuario en la lista.
     */
    private final OnUserInteractionListener listener;

    /**
     * Constructor que inicializa el adaptador con el listener para manejar interacciones.
     *
     * @param listener Implementación de {@link OnUserInteractionListener} para reaccionar a acciones del usuario.
     */
    public UserAdapter(OnUserInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout de cada ítem de usuario.
     *
     * @param parent   Contenedor padre al que se añadirá la vista.
     * @param viewType Tipo de vista (no utilizado aquí, siempre es el mismo layout).
     * @return Instancia de {@link UserViewHolder} con la vista inflada.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        ItemUserBinding b = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(b);
    }

    /**
     * Vincula los datos de un {@link UsuarioDTO} al ViewHolder correspondiente,
     * configurando textos y listeners para los botones de editar, alternar admin y eliminar.
     *
     * @param holder ViewHolder que contiene las vistas de cada ítem.
     * @param pos    Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int pos) {
        UsuarioDTO u = getItem(pos);
        holder.binding.tvUsername.setText(u.getUsername());
        holder.binding.tvEmail.setText(u.getCorreo());
        holder.binding.tvAdminStatus.setText(
                u.isAdmin() ? R.string.admin : R.string.user
        );

        // Botón para editar usuario
        holder.binding.btnEditUser.setOnClickListener(v ->
                listener.onEditUser(u)
        );

        // Texto dinámico y acción para alternar privilegios de administrador
        String toggleText = u.isAdmin()
                ? holder.binding.getRoot().getContext().getString(R.string.action_remove_admin)
                : holder.binding.getRoot().getContext().getString(R.string.action_toggle_admin);
        holder.binding.btnToggleAdmin.setText(toggleText);
        holder.binding.btnToggleAdmin.setOnClickListener(v ->
                listener.onToggleAdmin(u, !u.isAdmin())
        );

        // Botón para eliminar usuario
        holder.binding.btnDeleteUser.setOnClickListener(v ->
                listener.onDeleteUser(u)
        );
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada elemento de usuario.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        /**
         * Binding generado para acceder a las vistas del layout item_user.xml.
         */
        final ItemUserBinding binding;

        /**
         * Construye el ViewHolder usando el binding inflado.
         *
         * @param b Binding de la vista de cada ítem.
         */
        UserViewHolder(ItemUserBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
