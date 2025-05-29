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

public class AdminUsersFragment extends Fragment implements UserAdapter.OnUserInteractionListener {
    private FragmentAdminUsersBinding binding;
    private UserViewModel viewModel;
    private UserAdapter adapter;
    private AlertDialog currentDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Sólo administradores
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

        // RecyclerView + FAB
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);
        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());



        // Observers
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
            String email  = dlgB.etEmail   .getText().toString().trim();
            String name   = dlgB.etNombre  .getText().toString().trim();
            String last   = dlgB.etApellido.getText().toString().trim();
            String pass   = dlgB.etPassword.getText().toString();
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

            viewModel.addUser(dto);
        });
    }

    private void showEditUserDialog(@NonNull UsuarioDTO user) {
        DialogAddUserBinding dlgB = DialogAddUserBinding.inflate(
                LayoutInflater.from(requireContext())
        );
        // Precargamos datos
        dlgB.etUsername .setText(user.getUsername());
        dlgB.etEmail    .setText(user.getCorreo());
        dlgB.etNombre   .setText(user.getNombre());
        dlgB.etApellido .setText(user.getApellido());
        dlgB.etPassword .setVisibility(View.GONE);
        dlgB.switchAdmin.setChecked(user.isAdmin());
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
            String email    = dlgB.etEmail   .getText().toString().trim();
            String name     = dlgB.etNombre  .getText().toString().trim();
            String last     = dlgB.etApellido.getText().toString().trim();
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

            viewModel.updateUser(user.getIdUsuario(), dto);
        });
    }

    @Override
    public void onEditUser(UsuarioDTO user) {
        showEditUserDialog(user);
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        currentDialog = null;
    }
}
