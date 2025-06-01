package com.proyecto.facilgimapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.DialogAddUserBinding;
import com.proyecto.facilgimapp.databinding.FragmentAdminUsersBinding;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.util.EmailValidator;
import com.proyecto.facilgimapp.util.PasswordValidator;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.UserViewModel;

/**
 * Fragment encargado de la administración de usuarios en la aplicación.
 * Permite listar usuarios, agregar nuevos, editar datos existentes,
 * alternar privilegios de administrador y eliminar usuarios.
 * Solo los usuarios con rol de administrador pueden acceder a esta pantalla.
 * 
 * Autor: Francisco Santana
 */
public class AdminUsersFragment extends Fragment implements UserAdapter.OnUserInteractionListener {
    /**
     * Binding de la vista correspondiente al layout de administración de usuarios.
     */
    private FragmentAdminUsersBinding binding;

    /**
     * ViewModel responsable de las operaciones sobre datos de usuario.
     */
    private UserViewModel viewModel;

    /**
     * Adaptador para el RecyclerView que muestra la lista de usuarios.
     */
    private UserAdapter adapter;

    /**
     * Referencia al diálogo actualmente abierto (agregar o editar usuario).
     */
    private AlertDialog currentDialog;

    /**
     * Infl a y retorna la vista del fragment. Se infla el layout definido en
     * {@link FragmentAdminUsersBinding}.
     *
     * @param inflater           Inflador de vistas de Android.
     * @param container          Contenedor padre en el que se insertará el fragmento.
     * @param savedInstanceState Estado previo del fragmento, puede ser {@code null}.
     * @return Vista raíz del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Verifica que el usuario
     * tenga permisos de administrador; en caso contrario muestra un diálogo de
     * error y navega hacia atrás. Si es administrador, inicializa el ViewModel,
     * configura el RecyclerView y sus observadores, y realiza la carga inicial
     * de la lista de usuarios.
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado previo del fragmento, puede ser {@code null}.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Solo administradores pueden acceder
        if (!SessionManager.isAdmin(requireContext())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_admin_only)
                    .setPositiveButton(android.R.string.ok, (d, w) ->
                            NavHostFragment.findNavController(this).navigateUp())
                    .show();
            return;
        }

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adapter   = new UserAdapter(this);

        // Configuración de RecyclerView y botón de agregar usuario
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);
        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());

        // Observadores de LiveData en el ViewModel
        viewModel.getUsers().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.opSuccess().observe(getViewLifecycleOwner(), ok -> {
            if (Boolean.TRUE.equals(ok) && currentDialog != null && currentDialog.isShowing()) {
                currentDialog.dismiss();
            }
        });
        viewModel.opError().observe(getViewLifecycleOwner(), err -> {
            if (err != null && currentDialog != null && currentDialog.isShowing()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.error)
                        .setMessage(err)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

        // Carga inicial de usuarios
        viewModel.loadUsers();
    }

    /**
     * Muestra un diálogo para agregar un nuevo usuario. Valida los campos del formulario,
     * crea un objeto {@link UsuarioDTO} con los datos ingresados y solicita al ViewModel
     * que lo agregue a la base de datos remota.
     */
    private void showAddUserDialog() {
        DialogAddUserBinding dlgB = DialogAddUserBinding.inflate(
                LayoutInflater.from(requireContext())
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_add_user)
                .setView(dlgB.getRoot())
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_add, null);

        currentDialog = builder.create();
        currentDialog.show();

        currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String user   = dlgB.etUsername.getText().toString().trim();
            String email  = dlgB.etEmail.getText().toString().trim();
            String name   = dlgB.etNombre.getText().toString().trim();
            String last   = dlgB.etApellido.getText().toString().trim();
            String pass   = dlgB.etPassword.getText().toString();
            String street = dlgB.etDireccion.getText().toString();
            boolean isAdmin = dlgB.switchAdmin.isChecked();

            boolean ok = true;
            if (TextUtils.isEmpty(user)) {
                dlgB.etUsername.setError(getString(R.string.error_required));
                ok = false;
            }
            if (TextUtils.isEmpty(email)) {
                dlgB.etEmail.setError(getString(R.string.error_required));
                ok = false;
            } else if (!EmailValidator.isValid(email)) {
                dlgB.etEmail.setError(getString(R.string.error_invalid_email));
                ok = false;
            }
            if (TextUtils.isEmpty(name)) {
                dlgB.etNombre.setError(getString(R.string.error_name_required));
                ok = false;
            }
            if (TextUtils.isEmpty(last)) {
                dlgB.etApellido.setError(getString(R.string.error_lastaname_required));
                ok = false;
            }
            if (TextUtils.isEmpty(pass)) {
                dlgB.etPassword.setError(getString(R.string.error_required));
                ok = false;
            } else if (!PasswordValidator.isValid(pass)) {
                dlgB.etPassword.setError(getString(R.string.error_invalid_password));
                ok = false;
            }
            if (!ok) return;

            UsuarioDTO dto = new UsuarioDTO();
            dto.setUsername(user);
            dto.setCorreo(email);
            dto.setNombre(name);
            dto.setApellido(last);
            dto.setPassword(pass);
            dto.setAdmin(isAdmin);
            dto.setDireccion(street);

            viewModel.addUser(dto);
        });
    }

    /**
     * Muestra un diálogo para editar un usuario existente. Precarga los datos del usuario
     * en el formulario, permite cambiar nombre, correo, apellido, dirección y privilegios de
     * administrador. Al confirmar, crea un {@link UsuarioDTO} con los cambios y solicita al
     * ViewModel la actualización.
     *
     * @param user Objeto {@link UsuarioDTO} que representa al usuario a editar.
     */
    private void showEditUserDialog(@NonNull UsuarioDTO user) {
        DialogAddUserBinding dlgB = DialogAddUserBinding.inflate(
                LayoutInflater.from(requireContext())
        );
        // Precarga de datos en los campos
        dlgB.etUsername.setText(user.getUsername());
        dlgB.etEmail.setText(user.getCorreo());
        dlgB.etNombre.setText(user.getNombre());
        dlgB.etApellido.setText(user.getApellido());
        dlgB.etPassword.setVisibility(View.GONE);
        dlgB.switchAdmin.setChecked(user.isAdmin());
        dlgB.etDireccion.setText(user.getDireccion());
        dlgB.switchAdmin.setText(R.string.hint_grant_admin);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_edit_user)
                .setView(dlgB.getRoot())
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_save, null);

        currentDialog = builder.create();
        currentDialog.show();

        currentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String userName = dlgB.etUsername.getText().toString().trim();
            String email    = dlgB.etEmail.getText().toString().trim();
            String name     = dlgB.etNombre.getText().toString().trim();
            String last     = dlgB.etApellido.getText().toString().trim();
            String street   = dlgB.etDireccion.getText().toString();
            boolean isAdmin = dlgB.switchAdmin.isChecked();

            boolean ok = true;
            if (TextUtils.isEmpty(userName)) {
                dlgB.etUsername.setError(getString(R.string.error_required));
                ok = false;
            }
            if (TextUtils.isEmpty(email)) {
                dlgB.etEmail.setError(getString(R.string.error_required));
                ok = false;
            } else if (!EmailValidator.isValid(email)) {
                dlgB.etEmail.setError(getString(R.string.error_invalid_email));
                ok = false;
            }
            if (TextUtils.isEmpty(name)) {
                dlgB.etNombre.setError(getString(R.string.error_required));
                ok = false;
            }
            if (TextUtils.isEmpty(last)) {
                dlgB.etApellido.setError(getString(R.string.error_required));
                ok = false;
            }
            if (!ok) return;

            UsuarioDTO dto = new UsuarioDTO();
            dto.setIdUsuario(user.getIdUsuario());
            dto.setUsername(userName);
            dto.setCorreo(email);
            dto.setNombre(name);
            dto.setApellido(last);
            dto.setAdmin(isAdmin);
            dto.setDireccion(street);

            viewModel.updateUser(user.getIdUsuario(), dto);
        });
    }

    /**
     * Callback que se invoca desde el adaptador cuando el usuario solicita editar
     * un usuario específico. Llama a {@link #showEditUserDialog(UsuarioDTO)}.
     *
     * @param user Usuario seleccionado para editar.
     */
    @Override
    public void onEditUser(UsuarioDTO user) {
        showEditUserDialog(user);
    }

    /**
     * Callback que se invoca desde el adaptador cuando se alterna el estado de
     * administrador de un usuario. Actualiza el objeto {@link UsuarioDTO} con
     * el nuevo estado y solicita la actualización al ViewModel.
     *
     * @param user      Usuario seleccionado para alternar permisos.
     * @param makeAdmin {@code true} para otorgar permisos de administrador,
     *                  {@code false} para revocarlos.
     */
    @Override
    public void onToggleAdmin(UsuarioDTO user, boolean makeAdmin) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(user.getIdUsuario());
        dto.setUsername(user.getUsername());
        dto.setCorreo(user.getCorreo());
        dto.setNombre(user.getNombre());
        dto.setApellido(user.getApellido());
        dto.setAdmin(makeAdmin);
        viewModel.updateUser(user.getIdUsuario(), dto);
    }

    /**
     * Callback que se invoca desde el adaptador cuando se solicita eliminar un usuario.
     * Muestra un diálogo de confirmación antes de invocar {@link UserViewModel#deleteUser(int)}.
     *
     * @param user Usuario seleccionado para eliminar.
     */
    @Override
    public void onDeleteUser(UsuarioDTO user) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(getString(R.string.confirm_delete_user, user.getUsername()))
                .setPositiveButton(R.string.action_delete, (d, w) ->
                        viewModel.deleteUser(user.getIdUsuario()))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /**
     * Se llama cuando la vista del fragmento se destruye. Libera la referencia al binding
     * y al diálogo actual para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        currentDialog = null;
    }
}
