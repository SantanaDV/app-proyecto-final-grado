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
import com.proyecto.facilgimapp.model.LoginRequest;
import com.proyecto.facilgimapp.model.LoginResponse;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.etUsername.getText().toString();
            String pass = binding.etPassword.getText().toString();
            viewModel.login(new LoginRequest(user, pass))
                    .observe(getViewLifecycleOwner(), this::handleLoginResponse);
        });

        binding.fabRegister.setOnClickListener(v -> {
            // Navega al fragmento de registro
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    private void handleLoginResponse(LoginResponse resp) {
        if (resp != null && resp.getToken() != null) {
            SessionManager.saveLoginData(
                    requireContext(),
                    resp.getToken(),
                    resp.getUsername(),
                    resp.getAuthorities()
            );
            Toast.makeText(requireContext(), resp.getMensaje(), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_homeFragment);

        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.error_login_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
