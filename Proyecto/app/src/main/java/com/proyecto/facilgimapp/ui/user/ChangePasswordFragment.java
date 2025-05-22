package com.proyecto.facilgimapp.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.proyecto.facilgimapp.databinding.FragmentSecuritySettingsBinding;
import com.proyecto.facilgimapp.util.PreferenceManager;

public class ChangePasswordFragment extends Fragment {

    private FragmentSecuritySettingsBinding binding;

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        // Aquí puedes inicializar tus EditTexts y el botón de actualización
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String current = binding.etCurrentPassword.getText().toString();
            String next    = binding.etNewPassword.getText().toString();
            String confirm = binding.etConfirmPassword.getText().toString();
            // Valida y llama a tu ViewModel/repository...
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
