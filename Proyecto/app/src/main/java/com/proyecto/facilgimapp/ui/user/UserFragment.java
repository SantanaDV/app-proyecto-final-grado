package com.proyecto.facilgimapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentUserBinding;
import com.proyecto.facilgimapp.model.entity.UserOptionItem;
import com.proyecto.facilgimapp.model.entity.UserOptionType;
import com.proyecto.facilgimapp.ui.activities.MainActivity;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que muestra las opciones de configuración del usuario, incluyendo:
 * <ul>
 *     <li>Visualización del nombre de usuario</li>
 *     <li>Cambio de tema (oscuro o claro) opcional, con alternancia manual y por sistema</li>
 *     <li>Despliegue de opciones de configuración (idioma, tamaño de fuente, tema de color, cambio de contraseña, etc.)</li>
 *     <li>Botón de cerrar sesión</li>
 * </ul>
 * Cada opción se presenta en un RecyclerView mediante {@link UserOptionsAdapter}.
 * 
 * Autor: Francisco Santana
 */
public class UserFragment extends Fragment implements UserOptionsAdapter.Listener {
    /**
     * Binding generado para acceder a las vistas definidas en fragment_user.xml.
     */
    private FragmentUserBinding binding;

    /**
     * Adaptador que muestra las distintas opciones de usuario en un RecyclerView.
     */
    private UserOptionsAdapter adapter;

    /**
     * Infla el layout del fragment y devuelve la vista raíz.
     *
     * @param inflater           Inflador de vistas proporcionado por Android.
     * @param container          Contenedor padre donde se insertará este fragmento.
     * @param savedInstanceState Bundle con el estado previo del fragmento; puede ser null.
     * @return Vista raíz inflada correspondiente a fragment_user.xml.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>Mostrar el nombre de usuario recuperado de {@link SessionManager}.</li>
     *     <li>Inicializar RecyclerView con {@link UserOptionsAdapter} y un LayoutManager vertical.</li>
     *     <li>Configurar el switch para usar tema del sistema y aplicar el modo oscuro según la preferencia.</li>
     *     <li>Manejar el clic en "Cerrar sesión" para limpiar credenciales y navegar al login.</li>
     * </ul>
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado anterior; puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mostrar nombre de usuario
        binding.tvUsername.setText(SessionManager.getUsername(requireContext()));

        // Configurar RecyclerView con sus opciones
        binding.rvUserOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserOptionsAdapter(
                requireContext(),
                getUserOptions(),
                this
        );
        binding.rvUserOptions.setAdapter(adapter);

        // Switch para alternar tema del sistema
        boolean useSystem = PreferenceManager.isUseSystemTheme(requireContext());
        binding.switchUseSystemTheme.setChecked(useSystem);
        applyDarkMode(useSystem);
        binding.switchUseSystemTheme.setOnCheckedChangeListener((btn, checked) -> {
            PreferenceManager.setUseSystemTheme(requireContext(), checked);
            applyDarkMode(checked);
            // Para aplicar cambio de tema: reiniciamos actividad volviendo a MainActivity
            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                if (!isAdded() || isRemoving()) return;
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.putExtra("navigate_to", R.id.userFragment);
                requireActivity().finish();
                requireActivity().overridePendingTransition(0, 0);
                startActivity(intent);
            });
        });


        // Botón para cerrar sesión: limpia credenciales y vuelve al login
        binding.btnLogout.setOnClickListener(v -> {
            SessionManager.clearLoginOnly(requireContext());
            Toast.makeText(requireContext(),
                    R.string.session_closed, Toast.LENGTH_SHORT).show();
            if (!isAdded() || isRemoving()) return;
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userFragment_to_loginFragment);
        });
    }

    /**
     * Aplica el modo oscuro o claro según la preferencia de "usar tema del sistema".
     * <ul>
     *     <li>Si useSystem es true, consulta la configuración actual del sistema y ajusta el modo oscuro.</li>
     *     <li>Guarda el estado resultante en {@link PreferenceManager#setDarkMode(Context, boolean)}.</li>
     * </ul>
     *
     * @param useSystem {@code true} para seguir el tema del sistema, {@code false} para no cambiarlo.
     */
    private void applyDarkMode(boolean useSystem) {
        if (useSystem) {
            int mode = requireContext().getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            boolean sysDark = (mode == Configuration.UI_MODE_NIGHT_YES);
            PreferenceManager.setDarkMode(requireContext(), sysDark);
        }
    }

    /**
     * Construye la lista de ítems de opciones de usuario que se mostrarán.
     * Incluye:
     * <ul>
     *     <li>Modo oscuro</li>
     *     <li>Tamaño de fuente</li>
     *     <li>Color de tema</li>
     *     <li>Idioma</li>
     *     <li>Cambio de contraseña</li>
     *     <li>Gestión de usuarios (solo si es administrador)</li>
     *     <li>Restablecer preferencias</li>
     * </ul>
     *
     * @return Lista de {@link UserOptionItem} que alimenta el adaptador.
     */
    private List<UserOptionItem> getUserOptions() {
        var opts = new ArrayList<UserOptionItem>();
        opts.add(new UserOptionItem(UserOptionType.DARK_MODE));
        opts.add(new UserOptionItem(UserOptionType.FONT_SIZE));
        opts.add(new UserOptionItem(UserOptionType.THEME_COLOR));
        opts.add(new UserOptionItem(UserOptionType.LANGUAGE));
        opts.add(new UserOptionItem(UserOptionType.CHANGE_PASSWORD));
        if (SessionManager.isAdmin(requireContext())) {
            opts.add(new UserOptionItem(UserOptionType.MANAGE_USERS));
        }
        opts.add(new UserOptionItem(UserOptionType.CLEAR_PREFERENCES));
        return opts;
    }

    /**
     * Callback cuando el usuario alterna manualmente el modo oscuro.
     * <ul>
     *     <li>Anula el uso del tema del sistema.</li>
     *     <li>Guarda la preferencia de modo oscuro en {@link PreferenceManager}.</li>
     *     <li>Recrea la actividad para aplicar el cambio.</li>
     * </ul>
     *
     * @param on {@code true} para activar modo oscuro, {@code false} para modo claro.
     */
    @Override
    public void onDarkModeToggled(boolean on) {
        PreferenceManager.setUseSystemTheme(requireContext(), false);
        binding.switchUseSystemTheme.setChecked(false);
        PreferenceManager.setDarkMode(requireContext(), on);
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded() || isRemoving()) return;
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.userFragment);
            requireActivity().finish();
            requireActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }

    /**
     * Callback cuando el usuario cambia el tamaño de fuente.
     * Guarda la nueva preferencia en {@link PreferenceManager} y recrea la actividad.
     *
     * @param size Valor entero que representa el tamaño de fuente seleccionado.
     */
    @Override
    public void onFontSizeChanged(int size) {
        PreferenceManager.setFontSize(requireContext(), size);
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded() || isRemoving()) return;
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.userFragment);
            requireActivity().finish();
            requireActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }

    /**
     * Callback cuando el usuario selecciona un nuevo color de tema.
     * Almacena el índice seleccionado y recrea la actividad para aplicar el tema.
     *
     * @param idx Índice del color de tema seleccionado.
     */
    @Override
    public void onThemeColorSelected(int idx) {
        PreferenceManager.setThemeColorIndex(requireContext(), idx);
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded() || isRemoving()) return;
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.userFragment);
            requireActivity().finish();
            requireActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }


    /**
     * Callback cuando el usuario cambia el idioma de la aplicación.
     * Guarda el código de idioma en {@link PreferenceManager} y recrea la actividad.
     *
     * @param code Cadena con el código de idioma (p. ej. "es", "en").
     */
    @Override
    public void onLanguageChanged(String code) {
        PreferenceManager.setLanguage(requireContext(), code);
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded() || isRemoving()) return;
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.userFragment);
            requireActivity().finish();
            requireActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }


    /**
     * Callback cuando el usuario solicita cambiar su contraseña.
     * Navega al fragmento de cambio de contraseña.
     */
    @Override
    public void onChangePassword() {
        if (!isAdded() || isRemoving()) return;
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_changePasswordFragment);
    }

    /**
     * Callback cuando el usuario, siendo administrador, solicita gestionar otros usuarios.
     * Navega al fragmento de administración de usuarios.
     */
    @Override
    public void onManageUsers() {
        if (!isAdded() || isRemoving()) return;
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_adminUserFragment);
    }

    /**
     * Callback cuando el usuario solicita restablecer todas las preferencias a sus valores predeterminados.
     * <ul>
     *     <li>Limpia todas las preferencias con {@link PreferenceManager#clearAll(Context)}.</li>
     *     <li>Restaura valores por defecto para tema del sistema, idioma, tamaño de fuente y color de tema.</li>
     *     <li>Aplica modo oscuro de sistema si corresponde.</li>
     *     <li>Muestra un Toast confirmando el restablecimiento y recrea la actividad.</li>
     * </ul>
     */
    @Override
    public void onClearPreferences() {
        PreferenceManager.clearAll(requireContext());
        PreferenceManager.setUseSystemTheme(requireContext(), true);
        PreferenceManager.setLanguage(requireContext(), "es");
        PreferenceManager.setFontSize(requireContext(), 2);
        PreferenceManager.setThemeColorIndex(requireContext(), 0);
        applyDarkMode(true);
        Toast.makeText(requireContext(),
                R.string.preferencias_restablecidas, Toast.LENGTH_SHORT).show();
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            if (!isAdded() || isRemoving()) return;
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("navigate_to", R.id.userFragment);
            requireActivity().finish();
            requireActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }



    /**
     * Se llama cuando la vista del fragmento se destruye. Libera la referencia al binding
     * para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
