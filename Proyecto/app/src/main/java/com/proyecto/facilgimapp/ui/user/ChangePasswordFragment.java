package com.proyecto.facilgimapp.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentSecuritySettingsBinding;
import com.proyecto.facilgimapp.util.PasswordValidator;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.UserViewModel;

public class ChangePasswordFragment extends Fragment {
    private FragmentSecuritySettingsBinding binding;
    private UserViewModel viewModel;
    private String username;
    private int userId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        username = SessionManager.getUsername(requireContext());
        userId   = SessionManager.getUserId(requireContext());

        // Observamos el resultado de validar contraseña actual
        viewModel.currentValid().observe(getViewLifecycleOwner(), valid -> {
            if (valid == null) return;
            if (valid) {
                // Si actual es OK, lanzamos cambio
                doChangePassword();
            } else {
                binding.etCurrentPassword.setError(getString(R.string.error_invalid_password));
            }
        });

        // Observamos el resultado del cambio
        viewModel.changed().observe(getViewLifecycleOwner(), changed -> {
            if (changed == null) return;
            if (changed) {
                Toast.makeText(requireContext(),
                        R.string.password_updated,
                        Toast.LENGTH_SHORT).show();
                binding.etCurrentPassword.setText("");
                binding.etNewPassword    .setText("");
                binding.etConfirmPassword.setText("");
            } else {
                Toast.makeText(requireContext(),
                        R.string.error_password_change,
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUpdatePassword.setOnClickListener(v -> attemptChange());
    }

    private void attemptChange() {
        // Limpiamos los errores
        binding.etCurrentPassword.setError(null);
        binding.etNewPassword    .setError(null);
        binding.etConfirmPassword.setError(null);

        String current = binding.etCurrentPassword.getText().toString().trim();
        String next    = binding.etNewPassword    .getText().toString().trim();
        String confirm = binding.etConfirmPassword.getText().toString().trim();

        // campos no vacíos
        if (TextUtils.isEmpty(current)
                || TextUtils.isEmpty(next)
                || TextUtils.isEmpty(confirm)) {
            Toast.makeText(requireContext(),
                    R.string.error_required_fields,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // nueva tiene que ser igual a confirm
        if (!next.equals(confirm)) {
            binding.etConfirmPassword
                    .setError(getString(R.string.error_passwords_not_match));
            return;
        }

        //  regex para comprobar que la contraseña cumple con los requisitos
        if (!PasswordValidator.isValid(next)) {
            binding.etNewPassword.setError(
                    getString(R.string.error_invalid_password)
            );
            return;
        }

        //  validamos la actual contra el backend
        viewModel.validateCurrentPassword(username, current);
    }

    /** Ya validada la actual, entra aquí */
    private void doChangePassword() {
        String newPwd = binding.etNewPassword.getText().toString().trim();
        viewModel.changePassword(userId, newPwd);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
