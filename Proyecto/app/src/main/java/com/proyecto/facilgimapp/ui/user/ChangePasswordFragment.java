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

/**
 * Fragment encargado de permitir al usuario cambiar su contraseña.
 * <p>
 * Contiene campos para ingresar la contraseña actual, la nueva contraseña y su confirmación.
 * Valida localmente que los campos no estén vacíos, que la nueva contraseña cumpla con los
 * requisitos de formato y que coincidan la nueva y su confirmación. Luego solicita al ViewModel
 * la validación de la contraseña actual y, si es correcta, efectúa el cambio en el backend.
 * </p>
 * 
 * @author Francisco Santana
 */
public class ChangePasswordFragment extends Fragment {
    /**
     * Binding generado para acceder a las vistas definidas en fragment_security_settings.xml.
     */
    private FragmentSecuritySettingsBinding binding;

    /**
     * ViewModel que gestiona la lógica de cambio de contraseña y comunicación con el repositorio.
     */
    private UserViewModel viewModel;

    /**
     * Nombre de usuario actual obtenido de sesión para validaciones.
     */
    private String username;

    /**
     * ID de usuario actual obtenido de sesión para realizar el cambio en el backend.
     */
    private int userId;

    /**
     * Infla el layout del fragment y devuelve la vista raíz.
     *
     * @param inflater           Inflador de vistas proporcionado por Android.
     * @param container          Contenedor padre en el que se insertará el fragmento.
     * @param savedInstanceState Bundle con el estado anterior del fragmento, puede ser null.
     * @return Vista raíz inflada correspondiente a fragment_security_settings.xml.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecuritySettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Configura:
     * <ul>
     *     <li>Inicialización de {@link UserViewModel}.</li>
     *     <li>Obtención de nombre de usuario e ID desde {@link SessionManager}.</li>
     *     <li>Observadores de LiveData para el resultado de la validación de la contraseña actual
     *         y para el resultado del cambio de contraseña.</li>
     *     <li>Listener para el botón de actualizar contraseña que llama a {@link #attemptChange()}.</li>
     * </ul>
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado anterior, puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        username = SessionManager.getUsername(requireContext());
        userId   = SessionManager.getUserId(requireContext());

        // Observamos el resultado de validar contraseña actual
        viewModel.currentValid().observe(getViewLifecycleOwner(), valid -> {
            if (valid == null) return;
            if (valid) {
                // Si la contraseña actual es correcta, procedemos al cambio
                doChangePassword();
            } else {
                binding.etCurrentPassword.setError(getString(R.string.error_invalid_password));
            }
        });

        // Observamos el resultado del cambio de contraseña
        viewModel.changed().observe(getViewLifecycleOwner(), changed -> {
            if (changed == null) return;
            if (changed) {
                Toast.makeText(requireContext(),
                        R.string.password_updated,
                        Toast.LENGTH_SHORT).show();
                // Limpiamos los campos tras un cambio exitoso
                binding.etCurrentPassword.setText("");
                binding.etNewPassword.setText("");
                binding.etConfirmPassword.setText("");
            } else {
                Toast.makeText(requireContext(),
                        R.string.error_password_change,
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUpdatePassword.setOnClickListener(v -> attemptChange());
    }

    /**
     * Inicia el proceso de cambio de contraseña. Realiza validaciones locales:
     * <ul>
     *     <li>Campos no vacíos.</li>
     *     <li>Nueva contraseña igual a su confirmación.</li>
     *     <li>Nueva contraseña cumple con el formato mínimo según {@link PasswordValidator}.</li>
     * </ul>
     * Si todas las validaciones pasan, solicita al ViewModel la validación de la contraseña actual.
     */
    private void attemptChange() {
        // Limpiamos los errores previos
        binding.etCurrentPassword.setError(null);
        binding.etNewPassword.setError(null);
        binding.etConfirmPassword.setError(null);

        String current = binding.etCurrentPassword.getText().toString().trim();
        String next    = binding.etNewPassword.getText().toString().trim();
        String confirm = binding.etConfirmPassword.getText().toString().trim();

        // Verificar que ningún campo esté vacío
        if (TextUtils.isEmpty(current)
                || TextUtils.isEmpty(next)
                || TextUtils.isEmpty(confirm)) {
            Toast.makeText(requireContext(),
                    R.string.error_required_fields,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que la nueva contraseña y su confirmación coincidan
        if (!next.equals(confirm)) {
            binding.etConfirmPassword
                    .setError(getString(R.string.error_passwords_not_match));
            return;
        }

        // Verificar que la nueva contraseña cumpla con los requisitos de formato
        if (!PasswordValidator.isValid(next)) {
            binding.etNewPassword.setError(
                    getString(R.string.error_invalid_password)
            );
            return;
        }

        // Solicitar validación de la contraseña actual al backend
        viewModel.validateCurrentPassword(username, current);
    }

    /**
     * Se ejecuta una vez validada la contraseña actual. Obtiene el texto de la nueva contraseña
     * y solicita al ViewModel el cambio efectivo en el backend.
     */
    private void doChangePassword() {
        String newPwd = binding.etNewPassword.getText().toString().trim();
        viewModel.changePassword(userId, newPwd);
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
