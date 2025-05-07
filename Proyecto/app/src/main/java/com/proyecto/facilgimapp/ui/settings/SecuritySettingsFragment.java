package com.proyecto.facilgimapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.proyecto.facilgimapp.databinding.FragmentSecuritySettingsBinding;

public class SecuritySettingsFragment extends Fragment {
    private FragmentSecuritySettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String current = binding.etCurrentPassword.getText().toString();
            String nuevo = binding.etNewPassword.getText().toString();
            String confirm = binding.etConfirmPassword.getText().toString();
            // Validar y actualizar contraseña :contentReference[oaicite:4]{index=4}:contentReference[oaicite:5]{index=5}
        });
    }
}
