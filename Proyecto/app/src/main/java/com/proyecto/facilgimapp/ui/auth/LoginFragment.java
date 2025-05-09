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
import com.proyecto.facilgimapp.databinding.FragmentLoginBinding;
import com.proyecto.facilgimapp.model.dto.LoginRequest;
import com.proyecto.facilgimapp.model.dto.LoginResponse;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private boolean remember;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Rellenar campos si las credenciales guardadas existen
        String savedUser = SessionManager.getSavedUsername(requireContext());
        String savedPass = SessionManager.getSavedPassword(requireContext());
        if (savedUser != null && savedPass != null) {
            binding.etUsername.setText(savedUser);
            binding.etPassword.setText(savedPass);
            binding.sRemember.setChecked(true);
        }

        binding.btnLogin.setOnClickListener(v -> {

            String user = binding.etUsername.getText().toString();
            String pass = binding.etPassword.getText().toString();
            remember = binding.sRemember.isChecked();

            viewModel.login(new LoginRequest(user, pass))
                    .observe(getViewLifecycleOwner(), resp -> {
                        if (resp != null && resp.getToken() != null) {
                            // Guardar token, usuario, id, etc.
                            SessionManager.saveLoginData(
                                    requireContext(),
                                    resp.getToken(),
                                    resp.getUsername(),
                                    resp.getAuthorities(),
                                    resp.getUserId()
                            );

                            // Guardar credenciales según el switch
                            if (remember) {
                                SessionManager.saveCredentials(requireContext(), user, pass);
                            } else {
                                SessionManager.clearCredentials(requireContext());
                            }

                            Toast.makeText(requireContext(),
                                    resp.getMensaje(),
                                    Toast.LENGTH_SHORT).show();
                            // Navegar al Home
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_loginFragment_to_homeFragment);
                        } else {
                            Toast.makeText(requireContext(),
                                    getString(R.string.error_login_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        binding.fabRegister.setOnClickListener(v -> {
            // Navegar al fragmento de registro
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
