package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.proyecto.facilgimapp.R;

public class PreferenceManager {
    private static final String PREFS_NAME = "facilgim_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_APP_COLOR = "app_color";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isDarkModeEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkMode(Context ctx, boolean enabled) {
        prefs(ctx).edit()
                .putBoolean(KEY_DARK_MODE, enabled)
                .apply();
    }

    public static int getFontSize(Context ctx) {
        return prefs(ctx).getInt(KEY_FONT_SIZE, 1);
    }

    public static void setFontSize(Context ctx, int size) {
        prefs(ctx).edit()
                .putInt(KEY_FONT_SIZE, size)
                .apply();
    }

    public static int getAppColor(Context ctx) {
        return prefs(ctx).getInt(KEY_APP_COLOR, R.style.Theme_FacilGimApp);
    }

    public static void setAppColor(Context ctx, int themeResId) {
        prefs(ctx).edit()
                .putInt(KEY_APP_COLOR, themeResId)
                .apply();
    }
}
