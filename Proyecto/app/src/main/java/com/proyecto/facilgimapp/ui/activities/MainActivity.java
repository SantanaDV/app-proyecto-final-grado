package com.proyecto.facilgimapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.network.ConnectionState;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.util.ThemeUtils;

import java.util.Set;

public class MainActivity extends BaseActivity {
    private NavController navController;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) NavController
        NavHostFragment host = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (host == null) finish();
        navController = host.getNavController();

        // 2) Forzar logout si procede
        if (getIntent().getBooleanExtra("forceLogout", false)) {
            navController.navigate(R.id.loginFragment);
        }

        // 3) Observa conexión/sesión
        ConnectionState.get().isNetworkUp().observe(this, up -> {
            if (!up) {
                SessionManager.clearLoginOnly(this);
                Integer cur = navController.getCurrentDestination() != null
                        ? navController.getCurrentDestination().getId()
                        : null;
                if (cur != null
                        && cur != R.id.loginFragment
                        && cur != R.id.registerFragment) {
                    navController.popBackStack(R.id.loginFragment, false);
                    navController.navigate(R.id.loginFragment);
                }
            }
        });

        // 4) Toolbar (sólo espacio)
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 5) BottomNav + AppBarConfiguration
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration config = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.workoutsFragment,
                R.id.exercisesFragment,
                R.id.userFragment
        ).build();
        NavigationUI.setupActionBarWithNavController(this, navController, config);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // 6) Pantallas
        Set<Integer> bottomVisible = Set.of(
                R.id.homeFragment,
                R.id.workoutsFragment,
                R.id.exercisesFragment,
                R.id.userFragment
        );
        Set<Integer> toolbarContent = Set.of(
                R.id.workoutsFragment,
                R.id.exercisesFragment,
                R.id.newWorkoutFragment,
                R.id.typeListFragment,
                R.id.workoutDetailFragment,
                R.id.workoutSessionFragment,
                R.id.adminUserFragment
        );

        // 7) Cada vez que cambias de fragment:
        navController.addOnDestinationChangedListener((ctl, dest, args) -> {
            int id = dest.getId();

            // — BottomNav
            bottomNav.setVisibility(
                    bottomVisible.contains(id) ? View.VISIBLE : View.GONE
            );

            // — Color de la status bar
            @ColorInt int statusColor = ThemeUtils.resolveColor(
                    this,
                    com.google.android.material.R.attr.colorPrimary
            );
            getWindow().setStatusBarColor(statusColor);

            // — Toolbar sólo en los destinos que la usan
            if (toolbarContent.contains(id)) {
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());
            } else {
                toolbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
