package com.proyecto.facilgimapp.ui.activities;

import android.os.Bundle;
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

public class MainActivity extends BaseActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Obtener el NavController
        NavHostFragment host = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (host == null) {
            finish();
            return;
        }
        navController = host.getNavController();

        // 2) Si venimos forzados a logout...
        if (getIntent().getBooleanExtra("forceLogout", false)) {
            navController.navigate(R.id.loginFragment);
        }

        // 3) Observador de estado de red/servidor
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
                    // vaciamos back-stack hasta login y navegamos ahí
                    navController.popBackStack(R.id.loginFragment, false);
                    navController.navigate(R.id.loginFragment);
                }
                Toast.makeText(this,
                        "No hay conexión o sesión inválida",
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
            boolean show = destination.getId() != R.id.loginFragment
                    && destination.getId() != R.id.registerFragment;
            toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
            bottomNav.setVisibility(show ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
