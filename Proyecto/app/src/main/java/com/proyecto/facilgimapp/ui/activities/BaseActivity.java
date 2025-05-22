package com.proyecto.facilgimapp.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.util.PreferenceManager;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 3) Tema de color
        int themeColor = PreferenceManager.getThemeColor(this);
        if (themeColor == R.drawable.circle_green)      setTheme(R.style.Theme_Green);
        else if (themeColor == R.drawable.circle_yellow) setTheme(R.style.Theme_Yellow); // o crea Theme_Yellow
        else                                             setTheme(R.style.Theme_Blue);

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
