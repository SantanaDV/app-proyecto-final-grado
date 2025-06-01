package com.proyecto.facilgimapp.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.util.PreferenceManager;

import java.util.Locale;

/**
 * Actividad base de la aplicación que aplica configuraciones globales
 * como el idioma, tamaño de fuente, tema de color y modo noche antes
 * de crear la interfaz de usuario. Todas las Activities deben heredar
 * de esta clase para mantener un comportamiento consistente en toda la app.
 * 
 * Autor: Francisco Santana
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Se ejecuta antes de onCreate y permite ajustar la configuración
     * de contexto según las preferencias del usuario:
     * <ol>
     *     <li>Selecciona el idioma almacenado en {@link PreferenceManager#getLanguage(Context)}</li>
     *     <li>Aplica la escala de fuente según {@link PreferenceManager#getFontSize(Context)}</li>
     * </ol>
     *
     * @param newBase Contexto original sin modificaciones.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // 1) Locale
        String lang = PreferenceManager.getLanguage(newBase);
        Locale locale = new Locale(lang);
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);

        // 2) Font scale
        int fontPref = PreferenceManager.getFontSize(newBase);
        float scale = fontPref == 1 ? 0.9f : fontPref == 3 ? 1.1f : 1f;
        config.fontScale = scale;

        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    /**
     * Se ejecuta en la creación de la Activity y aplica:
     * <ol>
     *     <li>El tema de color seleccionado en {@link PreferenceManager#getThemeColorIndex(Context)}</li>
     *     <li>El modo noche (sistema, habilitado o deshabilitado) según {@link PreferenceManager}</li>
     * </ol>
     *
     * @param savedInstanceState Bundle con el estado previo de la Activity, puede ser null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 3) Tema de color
        int idx  = PreferenceManager.getThemeColorIndex(this);
        switch (idx) {
            case 1:
                setTheme(R.style.Theme_Green);
                break;
            case 2:
                setTheme(R.style.Theme_Yellow);
                break;
            default:
                setTheme(R.style.Theme_FacilGimApp);
        }

        // 4) Modo noche
        if (PreferenceManager.isUseSystemTheme(this)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            );
        } else if (PreferenceManager.isDarkModeEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        super.onCreate(savedInstanceState);
    }
}
