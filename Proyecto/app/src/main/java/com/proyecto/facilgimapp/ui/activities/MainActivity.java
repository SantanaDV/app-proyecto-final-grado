package com.proyecto.facilgimapp.ui.activities;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.network.ConnectionState;
import com.proyecto.facilgimapp.util.SessionManager;

import java.util.Set;

/**
 * Actividad principal de la aplicación que gestiona la navegación entre fragments,
 * la interfaz de usuario (toolbar y BottomNavigationView) y controla el estado de
 * la conexión de red para forzar logout si es necesario.
 * <p>
 * Desde aquí se establece el NavController, se suscribe al observador de estado
 * de red/servidor y se configura la visibilidad de la barra superior y menú inferior
 * según el fragmento activo. También maneja una posible redirección forzada al login
 * cuando se recibe el flag correspondiente.
 * </p>
 * 
 * @author Francisco Santana
 */
public class MainActivity extends BaseActivity {
    /**
     * Controlador de navegación para gestionar los fragmentos.
     */
    private NavController navController;

    /**
     * Conjunto de identificadores de fragments en los que se debe mostrar
     * el BottomNavigationView.
     */
    private static final Set<Integer> SHOW_IN = Set.of(
            R.id.homeFragment,
            R.id.userFragment,
            R.id.exercisesFragment,
            R.id.workoutsFragment
    );

    /**
     * Se invoca al crear la actividad. Configura la navegación, la observación del
     * estado de la conexión, la toolbar y el BottomNavigationView, así como la lógica
     * de mostrar u ocultar elementos de la interfaz según el fragmento actual.
     *
     * @param savedInstanceState Bundle con el estado previo de la Activity, puede ser null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Obtener el NavController
        NavHostFragment host = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (host == null) {
            finish();
            return;
        }
        navController = host.getNavController();

        // Si venimos forzados a logout
        if (getIntent().getBooleanExtra("forceLogout", false)) {
            navController.navigate(R.id.loginFragment);
        }
        int destino = getIntent().getIntExtra("navigate_to", -1);
        if (destino != -1) {
            navController.navigate(destino);
        }
        // Observadomos el de estado de red/servidor
        ConnectionState.get().isNetworkUp().observe(this, up -> {
            if (!up) {
                // Limpiamos credenciales de sesión
                SessionManager.clearLoginOnly(this);
                // Sólo navegamos si NO estamos ya en login o register
                Integer currentId = navController.getCurrentDestination() != null
                        ? navController.getCurrentDestination().getId()
                        : null;
                if (currentId != null
                        && currentId != R.id.loginFragment
                        && currentId != R.id.registerFragment) {
                    // Vaciamos back-stack hasta login y navegamos ahí
                    navController.popBackStack(R.id.loginFragment, false);
                    navController.navigate(R.id.loginFragment);
                }
                Toast.makeText(this,
                        R.string.no_internet,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 4) Toolbar y BottomNavigationView
        Toolbar toolbar = findViewById(R.id.toolbarMain);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        AppBarConfiguration config = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.workoutsFragment,
                R.id.exercisesFragment,
                R.id.userFragment
        ).build();
        NavigationUI.setupActionBarWithNavController(this, navController, config);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // 5) Mostrar/ocultar UI según fragmento
        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            int id = destination.getId();

            // Toolbar solo ocultar en login, user y home
            boolean toolbarVisible = (id != R.id.loginFragment && id != R.id.homeFragment && id != R.id.userFragment);
            toolbar.setVisibility(toolbarVisible ? View.VISIBLE : View.GONE);

            // BottomNav solo mostrar en los fragments que corresponden
            boolean bottomNavVisible = SHOW_IN.contains(id);
            bottomNav.setVisibility(bottomNavVisible ? View.VISIBLE : View.GONE);
        });

        // Este listener adicional asegura la visibilidad correcta del BottomNavigationView
        navController.addOnDestinationChangedListener(
                (controller, destination, args) -> {
                    int id = destination.getId();
                    boolean show = SHOW_IN.contains(id);
                    // BottomNav
                    bottomNav.setVisibility(show ? View.VISIBLE : View.GONE);
                }
        );
    }

    /**
     * Maneja el evento de navegación "up" (flecha hacia atrás en la toolbar) utilizando
     * el NavController.
     *
     * @return {@code true} si la navegación "up" fue procesada por el NavController,
     * {@code false} en caso contrario.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
