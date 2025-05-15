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
import com.proyecto.facilgimapp.util.AppContextProvider;
import com.proyecto.facilgimapp.util.PreferenceManager;

/**
 * Activity principal que contiene el NavHostFragment y la BottomNavigationView.
 */
public class MainActivity extends AppCompatActivity {
    private NavController navController;

    public NavController getNavController() {
        return navController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema antes de inflar la vista
        setTheme(PreferenceManager.getAppColor(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getIntent().getBooleanExtra("forceLogout", false)) {
            NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (host != null) {
                NavController navController = host.getNavController();
                navController.navigate(R.id.loginFragment);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        // Quitar el título automáticamente
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
                    R.id.userFragment
            ).build();
            //NavigationUI.setupWithNavController(toolbar, navController, config);
            NavigationUI.setupActionBarWithNavController(this, navController, config);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Aquí se oculta/enseña toolbar y bottomNav según destino
            navController.addOnDestinationChangedListener((controller, destination, args) -> {
                boolean showToolbarAndBottomNav = !(destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment);
                toolbar.setVisibility(showToolbarAndBottomNav ? View.VISIBLE : View.GONE);
                bottomNav.setVisibility(showToolbarAndBottomNav ? View.VISIBLE : View.GONE);
            });

            AppContextProvider.set(this);

        }
    }
}