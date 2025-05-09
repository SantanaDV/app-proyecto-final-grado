package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.proyecto.facilgimapp.R;
import java.util.Locale;

public class PreferenceManager {
    private static final String PREFS_NAME = "facilgim_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_APP_COLOR = "app_color";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_USE_SYSTEM_THEME = "use_system_theme";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Obtener el tema oscuro o claro
    public static boolean isDarkModeEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DARK_MODE, false);
    }

    // Establecer el tema oscuro o claro
    public static void setDarkMode(Context ctx, boolean enabled) {
        prefs(ctx).edit()
                .putBoolean(KEY_DARK_MODE, enabled)
                .apply();
    }

    // Obtener el tamaño de la fuente
    public static int getFontSize(Context ctx) {
        return prefs(ctx).getInt(KEY_FONT_SIZE, 1);
    }

    // Establecer el tamaño de la fuente
    public static void setFontSize(Context ctx, int size) {
        prefs(ctx).edit()
                .putInt(KEY_FONT_SIZE, size)
                .apply();
    }

    // Obtener el color de la aplicación
    public static int getAppColor(Context ctx) {
        return prefs(ctx).getInt(KEY_APP_COLOR, R.style.Theme_FacilGimApp);
    }

    // Establecer el color de la aplicación
    public static void setAppColor(Context ctx, int themeResId) {
        prefs(ctx).edit()
                .putInt(KEY_APP_COLOR, themeResId)
                .apply();
    }

    // Obtener el idioma de la aplicación
    public static String getLanguage(Context ctx) {
        return prefs(ctx).getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
    }

    // Establecer el idioma de la aplicación
    public static void setLanguage(Context ctx, String language) {
        prefs(ctx).edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    // Obtener si usa el tema del sistema
    public static boolean useSystemTheme(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_SYSTEM_THEME, true);
    }

    // Establecer si usa el tema del sistema
    public static void setUseSystemTheme(Context ctx, boolean useSystemTheme) {
        prefs(ctx).edit()
                .putBoolean(KEY_USE_SYSTEM_THEME, useSystemTheme)
                .apply();
    }

    // Limpiar todas las preferencias
    public static void clearAll(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }
}
