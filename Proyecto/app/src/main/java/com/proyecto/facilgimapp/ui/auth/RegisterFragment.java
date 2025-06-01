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

/**
 * Fragment que gestiona la interfaz de registro de nuevos usuarios.
 * <p>
 * Presenta un formulario con campos para nombre de usuario, contraseña, nombre,
 * apellido, correo electrónico y dirección. Valida los datos ingresados y,
 * si son correctos, delega la creación de usuario al ViewModel. Muestra mensajes
 * de error o éxito según el resultado de la operación.
 * </p>
 * 
 * @author Francisco Santana
 */
public class RegisterFragment extends Fragment {
    /**
     * Binding generado para acceder a las vistas del layout fragment_register.xml.
     */
    private FragmentRegisterBinding binding;

    /**
     * ViewModel que encapsula la lógica de negocio para el registro de usuarios.
     */
    private AuthViewModel viewModel;

    /**
     * Infla el layout del fragment y devuelve la vista raíz.
     *
     * @param inflater           Inflador de vistas de Android.
     * @param container          Contenedor padre en el que se insertará este fragmento.
     * @param savedInstanceState Bundle con el estado previo del fragmento, puede ser null.
     * @return Vista raíz del fragment inflada.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada. Inicializa el ViewModel,
     * configura el observador de errores de registro y establece el listener
     * para el botón de registro.
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado previo del fragmento, puede ser null.
     */
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

    /**
     * Valida los campos del formulario de registro:
     * <ul>
     *     <li>Nombre de usuario no vacío.</li>
     *     <li>Contraseña no vacía y cumple con los requisitos mínimos.</li>
     *     <li>Nombre y apellido no vacíos.</li>
     *     <li>Correo electrónico no vacío y con formato válido.</li>
     * </ul>
     *
     * @return {@code true} si todos los campos son válidos; {@code false} en caso contrario.
     */
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

    /**
     * Maneja la respuesta del intento de registro:
     * <ul>
     *     <li>Si el usuario se crea correctamente (ID no nulo), muestra un mensaje de éxito
     *         y navega al fragmento de login.</li>
     *     <li>Si la creación falla, muestra un mensaje de error genérico (el error específico
     *         ya se habría mostrado desde el observable).</li>
     * </ul>
     *
     * @param user DTO con los datos del usuario registrado; su ID será no nulo si fue exitoso.
     */
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
