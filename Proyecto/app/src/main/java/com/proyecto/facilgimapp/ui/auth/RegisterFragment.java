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
import com.proyecto.facilgimapp.util.EmailValidator;
import com.proyecto.facilgimapp.util.PasswordValidator;
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

        // Observador de errores de registro
        viewModel.getRegisterError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

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

                // Registrar y manejar resultado
                viewModel.register(dto)
                        .observe(getViewLifecycleOwner(), this::handleRegister);
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String name     = binding.etName.getText().toString().trim();
        String lastname = binding.etLastname.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();

        // Usuario obligatorio
        if (username.isEmpty()) {
            binding.etUsername.setError(getString(R.string.error_username_required));
            isValid = false;
        }

        // Contraseña: no vacía + cumple mínimos
        if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (!PasswordValidator.isValid(password)) {
            binding.etPassword.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        // Nombre y apellido
        if (name.isEmpty()) {
            binding.etName.setError(getString(R.string.error_name_required));
            isValid = false;
        }
        if (lastname.isEmpty()) {
            binding.etLastname.setError(getString(R.string.error_lastaname_required));
            isValid = false;
        }

        // Email: no vacío + formato
        if (email.isEmpty()) {
            binding.etEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!EmailValidator.isValid(email)) {
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
            // Ya se habrá mostrado el error concreto vía getRegisterError()
            Toast.makeText(requireContext(),
                    getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
