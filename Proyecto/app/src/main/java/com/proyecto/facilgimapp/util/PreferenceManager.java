package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.proyecto.facilgimapp.R;

import java.util.Locale;

/**
 * Gestor de preferencias compartidas de la aplicación.
 * <p>
 * Permite almacenar y recuperar configuraciones como:
 * <ul>
 *     <li>Modo oscuro.</li>
 *     <li>Tamaño de fuente.</li>
 *     <li>Índice de color de tema.</li>
 *     <li>Idioma de la interfaz.</li>
 *     <li>Uso de tema del sistema.</li>
 * </ul>
 * Proporciona métodos para leer y escribir cada preferencia, con valores por defecto razonables.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class PreferenceManager {
    private static final String PREFS_NAME           = "facilgim_prefs";
    private static final String KEY_DARK_MODE        = "dark_mode";
    private static final String KEY_FONT_SIZE        = "font_size";
    private static final String KEY_THEME_COLOR_IDX  = "theme_color_idx";
    private static final String KEY_LANGUAGE         = "language";
    private static final String KEY_USE_SYSTEM_THEME = "use_system_theme";

    /**
     * Obtiene la instancia de SharedPreferences con nombre {@link #PREFS_NAME} en modo privado.
     *
     * @param ctx Contexto de la aplicación, necesario para acceder a las preferencias.
     * @return Objeto {@link SharedPreferences} para leer y escribir.
     */
    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** 1) Dark Mode */

    /**
     * Indica si el modo oscuro está habilitado.
     *
     * @param ctx Contexto de la aplicación.
     * @return {@code true} si está activado, {@code false} en caso contrario.
     */
    public static boolean isDarkModeEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Activa o desactiva el modo oscuro.
     *
     * @param ctx     Contexto de la aplicación.
     * @param enabled {@code true} para habilitar, {@code false} para deshabilitar.
     */
    public static void setDarkMode(Context ctx, boolean enabled) {
        prefs(ctx).edit()
                .putBoolean(KEY_DARK_MODE, enabled)
                .apply();
    }

    /** 2) Font Size (1=small, 2=medium, 3=large). Por defecto medium=2 */

    /**
     * Obtiene el tamaño de fuente seleccionado.
     *
     * @param ctx Contexto de la aplicación.
     * @return Entero entre 1 y 3, donde 1=pequeño, 2=mediano, 3=grande. Por defecto 2.
     */
    public static int getFontSize(Context ctx) {
        return prefs(ctx).getInt(KEY_FONT_SIZE, 2);
    }

    /**
     * Establece el tamaño de fuente a usar.
     *
     * @param ctx  Contexto de la aplicación.
     * @param size Entero entre 1 y 3, donde 1=pequeño, 2=mediano, 3=grande.
     */
    public static void setFontSize(Context ctx, int size) {
        prefs(ctx).edit()
                .putInt(KEY_FONT_SIZE, size)
                .apply();
    }

    /** 3) Theme Color (almacena el índice de color) */

    /**
     * Obtiene el índice de color de tema seleccionado.
     *
     * @param ctx Contexto de la aplicación.
     * @return Índice entero (0, 1 o 2) que corresponde a los temas predefinidos.
     *         Por defecto 0.
     */
    public static int getThemeColorIndex(Context ctx) {
        return prefs(ctx).getInt(KEY_THEME_COLOR_IDX, 0);
    }

    /**
     * Establece el índice de color de tema a usar.
     *
     * @param ctx Contexto de la aplicación.
     * @param idx Índice entero (0, 1 o 2) para seleccionar el tema.
     */
    public static void setThemeColorIndex(Context ctx, int idx) {
        prefs(ctx).edit()
                .putInt(KEY_THEME_COLOR_IDX, idx)
                .apply();
    }

    /** 4) Language */

    /**
     * Obtiene el código de idioma seleccionado para la interfaz.
     *
     * @param ctx Contexto de la aplicación.
     * @return Cadena con el código ISO de idioma (p. ej., "es", "en").
     *         Por defecto, el idioma predeterminado del dispositivo.
     */
    public static String getLanguage(Context ctx) {
        return prefs(ctx).getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
    }

    /**
     * Cambia el idioma de la interfaz.
     *
     * @param ctx      Contexto de la aplicación.
     * @param language Código ISO del idioma a establecer (p. ej., "es", "en").
     */
    public static void setLanguage(Context ctx, String language) {
        prefs(ctx).edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    /** 5) Usar tema del sistema */

    /**
     * Indica si la aplicación debe seguir el tema oscuro del sistema.
     *
     * @param ctx Contexto de la aplicación.
     * @return {@code true} si debe usar el tema del sistema, {@code false} para usar ajustes manuales.
     *         Por defecto {@code true}.
     */
    public static boolean isUseSystemTheme(Context ctx) {
        return prefs(ctx).getBoolean(KEY_USE_SYSTEM_THEME, true);
    }

    /**
     * Establece si la aplicación debe respetar el tema oscuro del sistema o usar ajustes propios.
     *
     * @param ctx Contexto de la aplicación.
     * @param use {@code true} para usar el tema del sistema, {@code false} para ignorarlo.
     */
    public static void setUseSystemTheme(Context ctx, boolean use) {
        prefs(ctx).edit()
                .putBoolean(KEY_USE_SYSTEM_THEME, use)
                .apply();
    }

    /**
     * Limpia todas las preferencias almacenadas y restaura valores por defecto:
     * <ul>
     *     <li>Índice de color en 0.</li>
     *     <li>Los demás valores se borran por completo.</li>
     * </ul>
     *
     * @param ctx Contexto de la aplicación.
     */
    public static void clearAll(Context ctx) {
        prefs(ctx).edit().clear().apply();
        setThemeColorIndex(ctx, 0);
    }
}
