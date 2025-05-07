package com.proyecto.facilgimapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentProfileSettingsBinding;

public class ProfileSettingsFragment extends Fragment {
    private FragmentProfileSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Aquí cargarías tus datos de usuario (username, email…) y los colocarías en los campos
        binding.etUsername.setText("juanperez");    // Ejemplo :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
        binding.etEmail.setText("juan.perez@ejemplo.com");   // :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}

        binding.btnSaveProfile.setOnClickListener(v -> {
            // Lógica para guardar cambios de perfil
        });
    }
}
