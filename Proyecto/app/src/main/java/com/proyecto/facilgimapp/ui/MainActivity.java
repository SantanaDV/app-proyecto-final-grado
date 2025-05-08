package com.proyecto.facilgimapp.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.util.PreferenceManager;

/**
 * Activity principal que contiene el NavHostFragment y la BottomNavigationView.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema antes de inflar la vista
        setTheme(PreferenceManager.getAppColor(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        // Obtiene el NavController desde el NavHostFragment
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (host != null) {
            NavController navController = host.getNavController();

            // Define destinos de nivel superior (no mostrar flecha de Up)
            AppBarConfiguration config = new AppBarConfiguration.Builder(
                    R.id.homeFragment,
                    R.id.workoutsFragment,
                    R.id.exercisesFragment,
                    R.id.settingsFragment
            ).build();
            NavigationUI.setupWithNavController(toolbar, navController, config);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Aquí ocultas/enseñas toolbar y bottomNav según destino
            navController.addOnDestinationChangedListener((controller, destination, args) -> {
                if (destination.getId() == R.id.loginFragment
                        || destination.getId() == R.id.registerFragment) {
                    toolbar.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}