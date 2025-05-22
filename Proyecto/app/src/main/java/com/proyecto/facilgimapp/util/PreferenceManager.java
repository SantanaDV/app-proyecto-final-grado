package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.proyecto.facilgimapp.R;

import java.util.Locale;

public class PreferenceManager {
    private static final String PREFS_NAME           = "facilgim_prefs";
    private static final String KEY_DARK_MODE        = "dark_mode";
    private static final String KEY_FONT_SIZE        = "font_size";
    private static final String KEY_THEME_COLOR      = "theme_color";
    private static final String KEY_LANGUAGE         = "language";
    private static final String KEY_USE_SYSTEM_THEME = "use_system_theme";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** 1) Dark Mode */
    public static boolean isDarkModeEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DARK_MODE, false);
    }
    public static void setDarkMode(Context ctx, boolean enabled) {
        prefs(ctx).edit()
                .putBoolean(KEY_DARK_MODE, enabled)
                .apply();
    }

    /** 2) Font Size (1=small, 2=medium, 3=large). Por defecto medium=2 */
    public static int getFontSize(Context ctx) {
        return prefs(ctx).getInt(KEY_FONT_SIZE, 2);
    }
    public static void setFontSize(Context ctx, int size) {
        prefs(ctx).edit()
                .putInt(KEY_FONT_SIZE, size)
                .apply();
    }

    /** 3) Theme Color (almacena el drawable o style id) */
    public static int getThemeColor(Context ctx) {
        // Por defecto usaremos el círculo azul
        return prefs(ctx).getInt(KEY_THEME_COLOR, R.drawable.circle_blue);
    }
    public static void setThemeColor(Context ctx, int resId) {
        prefs(ctx).edit()
                .putInt(KEY_THEME_COLOR, resId)
                .apply();
    }

    /** 4) Language */
    public static String getLanguage(Context ctx) {
        return prefs(ctx).getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
    }
    public static void setLanguage(Context ctx, String language) {
        prefs(ctx).edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    /** 5) Usar tema del sistema */
    public static boolean isUseSystemTheme(Context ctx) {
        // true = respetar el modo oscuro del sistema
        return prefs(ctx).getBoolean(KEY_USE_SYSTEM_THEME, true);
    }
    public static void setUseSystemTheme(Context ctx, boolean use) {
        prefs(ctx).edit()
                .putBoolean(KEY_USE_SYSTEM_THEME, use)
                .apply();
    }

    /** Limpia todas las preferencias */
    public static void clearAll(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }
}