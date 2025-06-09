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
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentLoginBinding;
import com.proyecto.facilgimapp.model.dto.LoginRequest;
import com.proyecto.facilgimapp.model.dto.LoginResponse;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.AuthViewModel;

/**
 * Fragment encargado de manejar la pantalla de inicio de sesión.
 * <p>
 * Presenta un formulario con campos de usuario y contraseña, junto a un switch
 * para recordar las credenciales. Verifica si ya existe una sesión para evitar
 * mostrar el login innecesariamente. Al autenticarse correctamente, guarda el token
 * y los datos de sesión; según el switch, también guarda o borra las credenciales
 * en SharedPreferences.
 * </p>
 * 
 * @author Francisco Santana
 */
public class LoginFragment extends Fragment {
    /**
     * Binding generado para acceder a las vistas del layout fragment_login.xml.
     */
    private FragmentLoginBinding binding;

    /**
     * ViewModel que gestiona la lógica de autenticación y comunicación con el repositorio.
     */
    private AuthViewModel viewModel;

    /**
     * Indica si se debe guardar o no las credenciales ingresadas al iniciar sesión.
     */
    private boolean remember;

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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se invoca después de que la vista ha sido creada.
     * <ul>
     *     <li>Verifica si ya existe una sesión activa; en ese caso, navega directamente al Home.</li>
     *     <li>Inicializa el ViewModel de autenticación.</li>
     *     <li>Si hay credenciales guardadas, las precarga en los campos y marca el switch.</li>
     *     <li>Configura el listener del botón "Login" para procesar la autenticación:
     *         <ul>
     *             <li>Realiza la petición de login al ViewModel.</li>
     *             <li>En caso de éxito, guarda token y datos de usuario, muestra un Toast y navega al Home.</li>
     *             <li>En caso de error, muestra un Toast indicando fallo.</li>
     *         </ul>
     *     </li>
     *     <li>Configura el FAB para navegar al fragmento de registro.</li>
     * </ul>
     *
     * @param view               Vista previamente inflada devuelta por {@link #onCreateView}.
     * @param savedInstanceState Bundle con el estado previo del fragmento, puede ser null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Si ya hay sesión iniciada, redirigir al Home directamente
        if (SessionManager.isLoggedIn(requireContext())) {
            Navigation.findNavController(view).navigate(
                    R.id.action_loginFragment_to_homeFragment,
                    null,
                    new androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
            );
            return;
        }

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Rellena los campos si las credenciales guardadas existen
        String savedUser = SessionManager.getSavedUsername(requireContext());
        String savedPass = SessionManager.getSavedPassword(requireContext());
        if (savedUser != null && savedPass != null) {
            binding.etUsername.setText(savedUser);
            binding.etPassword.setText(savedPass);
            binding.sRemember.setChecked(true);
        }

        // Listener para el botón de inicio de sesión

        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.etUsername.getText().toString();
            String pass = binding.etPassword.getText().toString();
            if(user.isEmpty() && pass.isEmpty()){
                Toast.makeText(requireContext(), R.string.error_login_empty, Toast.LENGTH_SHORT).show();
                binding.etUsername.setError(getString(R.string.error_username_required));
                binding.etPassword.setError(getString(R.string.error_password_required));
                return;
            }
            if(user.isEmpty()){
                Toast.makeText(requireContext(), R.string.error_login_empty, Toast.LENGTH_SHORT).show();
                binding.etUsername.setError(getString(R.string.error_username_required));
                return;
            }
            if(pass.isEmpty()){{
                Toast.makeText(requireContext(), R.string.error_login_empty, Toast.LENGTH_SHORT).show();
                binding.etPassword.setError(getString(R.string.error_username_required));
                return;
            }}
            remember = binding.sRemember.isChecked();

            viewModel.login(new LoginRequest(user, pass))
                    .observe(getViewLifecycleOwner(), resp -> {
                        if (resp != null && resp.getToken() != null) {
                            // Guardar token y datos de sesión
                            SessionManager.saveLoginData(
                                    requireContext(),
                                    resp.getToken(),
                                    resp.getUsername(),
                                    resp.getAuthorities(),
                                    resp.getUserId()
                            );

                            // Guardar o borrar credenciales según el switch
                            if (remember) {
                                SessionManager.saveCredentials(requireContext(), user, pass);
                            } else {
                                SessionManager.clearCredentials(requireContext());
                            }

                            Toast.makeText(requireContext(),
                                    R.string.login_success,
                                    Toast.LENGTH_SHORT).show();
                            // Navegar al Home
                            NavController navController = Navigation.findNavController(requireView());
                            NavDestination currentDestination = navController.getCurrentDestination();
                            if (currentDestination != null && currentDestination.getId() == R.id.loginFragment) {
                                navController.navigate(
                                        R.id.action_loginFragment_to_homeFragment,
                                        null,
                                        new androidx.navigation.NavOptions.Builder()
                                                .setPopUpTo(R.id.loginFragment, true) // Elimina el LoginFragment del back stack
                                                .build()
                                );
                            } else {
                                Toast.makeText(requireContext(), R.string.no_podido_navegar, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Login fallido
                            Toast.makeText(requireContext(),
                                    getString(R.string.error_login_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // FAB para navegar al registro de usuario
        binding.fabRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
