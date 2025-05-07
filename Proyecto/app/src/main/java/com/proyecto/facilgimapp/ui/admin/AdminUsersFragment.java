package com.proyecto.facilgimapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentAdminUsersBinding;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.UserViewModel;

public class AdminUsersFragment extends Fragment implements UserAdapter.OnUserInteractionListener {
    private FragmentAdminUsersBinding binding;
    private UserViewModel viewModel;
    private UserAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!SessionManager.isAdmin(requireContext())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_admin_only)
                    .setPositiveButton(android.R.string.ok, (d, w) ->
                            NavHostFragment.findNavController(this).navigateUp()
                    )
                    .show();
            return;
        }

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adapter = new UserAdapter(this);

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);

        binding.toolbarUsers.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());

        viewModel.getUsers().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.loadUsers();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_user, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        builder.setTitle(R.string.title_add_user)
                .setView(dialogView)
                .setPositiveButton(R.string.action_add, (d, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String correo   = etEmail.getText().toString().trim();
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setUsername(username);
                    dto.setCorreo(correo);
                    viewModel.addUser(dto);
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDeleteUser(UsuarioDTO usuario) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(getString(R.string.confirm_delete_user, usuario.getUsername()))
                .setPositiveButton(R.string.action_delete, (d, which) ->
                        viewModel.deleteUser(usuario.getIdUsuario())
                )
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
