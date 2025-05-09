package com.proyecto.facilgimapp.ui.user;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentUserBinding;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.ui.user.adapters.UserOptionsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private UserOptionsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener datos del usuario
        String username = SessionManager.getUsername(requireContext());
        String email = SessionManager.getUserEmail(requireContext());

        binding.tvUsername.setText(username);
        binding.tvEmail.setText(email);

        // Configurar el RecyclerView con las opciones
        adapter = new UserOptionsAdapter(getContext(), this);
        binding.rvUserOptions.setAdapter(adapter);

        // Cargar las opciones según si el usuario es admin o no
        adapter.setUserOptions(getUserOptions());

        // Configurar el Switch de tema del sistema
        boolean useSystemTheme = PreferenceManager.useSystemTheme(requireContext());
        binding.switchUseSystemTheme.setChecked(useSystemTheme);

        if (useSystemTheme) {
            // Cambiar el tema según el sistema
            int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                PreferenceManager.setDarkMode(requireContext(), true); // Si el sistema tiene tema oscuro
            } else {
                PreferenceManager.setDarkMode(requireContext(), false); // Si el sistema tiene tema claro
            }
        } else {
            // Si no usa el tema del sistema, mantener el valor almacenado en las preferencias
            boolean isDarkMode = PreferenceManager.isDarkModeEnabled(requireContext());
            PreferenceManager.setDarkMode(requireContext(), isDarkMode);
        }

        // Listener para el cambio de tema
        binding.switchUseSystemTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceManager.setUseSystemTheme(requireContext(), isChecked);
            if (isChecked) {
                // Si está activado el tema del sistema, se ajustará el tema de la app según el sistema
                int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    PreferenceManager.setDarkMode(requireContext(), true);
                } else {
                    PreferenceManager.setDarkMode(requireContext(), false);
                }
            } else {
                // Si no usa el tema del sistema, aplicar el tema según la preferencia del usuario
                boolean isDarkMode = PreferenceManager.isDarkModeEnabled(requireContext());
                PreferenceManager.setDarkMode(requireContext(), isDarkMode);
            }
            getActivity().recreate();  // Recrear la actividad para aplicar el nuevo tema
        });

        // Cerrar sesión
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private List<String> getUserOptions() {
        List<String> options = new ArrayList<>();
        options.add("Cambiar Tema");
        options.add("Cambiar Idioma");
        options.add("Cambiar Nombre de Usuario");
        options.add("Cambiar Contraseña");
        options.add("Notificaciones");
        options.add("Privacidad");

        // Solo admins verán la opción para gestionar usuarios
        if (SessionManager.isAdmin(requireContext())) {
            options.add("Gestionar Usuarios");
        }

        return options;
    }

    // Gestión de las opciones
    public void onOptionSelected(String option) {
        switch (option) {
            case "Cambiar Tema":
                openAppearanceSettings();
                break;
            case "Cambiar Idioma":
                openLanguageSettings();
                break;
            case "Cambiar Nombre de Usuario":
                openUsernameChangeDialog();
                break;
            case "Cambiar Contraseña":
                openPasswordChangeDialog();
                break;
            case "Notificaciones":
                changeNotificationSettings();
                break;
            case "Privacidad":
                changePrivacySettings();
                break;
            case "Gestionar Usuarios":
                goToAdminUserFragment();
                break;
            default:
                break;
        }
    }

    private void openAppearanceSettings() {
        // Lógica para cambiar tema
        Toast.makeText(requireContext(), "Cambiar Tema", Toast.LENGTH_SHORT).show();
    }

    private void openLanguageSettings() {
        // Lógica para cambiar idioma
        Toast.makeText(requireContext(), "Cambiar Idioma", Toast.LENGTH_SHORT).show();
    }

    private void openUsernameChangeDialog() {
        // Lógica para cambiar nombre de usuario
        Toast.makeText(requireContext(), "Cambiar Nombre de Usuario", Toast.LENGTH_SHORT).show();
    }

    private void openPasswordChangeDialog() {
        // Lógica para cambiar contraseña
        Toast.makeText(requireContext(), "Cambiar Contraseña", Toast.LENGTH_SHORT).show();
    }

    private void changeNotificationSettings() {
        // Lógica para habilitar/deshabilitar notificaciones
        Toast.makeText(requireContext(), "Cambiar Notificaciones", Toast.LENGTH_SHORT).show();
    }

    private void changePrivacySettings() {
        // Lógica para habilitar/deshabilitar privacidad
        Toast.makeText(requireContext(), "Cambiar Privacidad", Toast.LENGTH_SHORT).show();
    }

    private void goToAdminUserFragment() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_adminUserFragment);
    }

    private void logout() {
        SessionManager.clearSession(requireContext());
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        // Navegar a la pantalla de login
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
