package com.proyecto.facilgimapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentRegisterBinding;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.viewmodel.AuthViewModel;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {
            // Validaciones de campos
            if (validateFields()) {
                UsuarioRequestDTO dto = new UsuarioRequestDTO();
                dto.setUsername(binding.etUsername.getText().toString().trim());
                dto.setPassword(binding.etPassword.getText().toString().trim());
                dto.setNombre(binding.etName.getText().toString().trim());
                dto.setApellido(binding.etLastname.getText().toString().trim());
                dto.setCorreo(binding.etEmail.getText().toString().trim());
                dto.setDireccion(binding.etAddress.getText().toString().trim());

                // Realizar el registro con el ViewModel
                viewModel.register(dto).observe(getViewLifecycleOwner(), this::handleRegister);
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;
        // Validación de campos obligatorios
        if (binding.etUsername.getText().toString().trim().isEmpty()) {
            binding.etUsername.setError(getString(R.string.error_username_required));
            isValid = false;
        }
        if (binding.etPassword.getText().toString().trim().isEmpty()) {
            binding.etPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        }
        if (binding.etEmail.getText().toString().trim().isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
            binding.etEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        return isValid;
    }

    private void handleRegister(UsuarioDTO user) {
        if (user != null && user.getIdUsuario() != null) {
            Toast.makeText(requireContext(),
                    getString(R.string.register_success), Toast.LENGTH_SHORT).show();
            // Navegar al LoginFragment después de un registro exitoso
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
        }
    }
}
