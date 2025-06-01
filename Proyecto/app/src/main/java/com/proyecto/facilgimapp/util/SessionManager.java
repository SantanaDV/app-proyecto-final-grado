package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.proyecto.facilgimapp.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SessionManager {
    private static final String PREFS_NAME       = "facilgim_session";
    private static final String KEY_TOKEN        = "key_token";
    private static final String KEY_USERNAME     = "key_username";
    private static final String KEY_AUTHORITIES  = "key_authorities";
    private static final String KEY_USER_ID      = "key_user_id";
    private static final String KEY_SAVED_USER   = "key_saved_user";
    private static final String KEY_SAVED_PASS   = "key_saved_pass";

    private static final String KEY_USER_EMAIL = "key_user_email";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Guarda token, usuario, roles e ID */
    public static void saveLoginData(Context ctx,
                                     String token,
                                     String username,
                                     List<String> authorities,
                                     int userId) {
        prefs(ctx).edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USERNAME, username)
                .putString(KEY_AUTHORITIES, String.join(",", authorities))
                .putInt(KEY_USER_ID, userId)
                .apply();
    }

    /** Recupera el JWT */
    public static String getToken(Context ctx) {
        return prefs(ctx).getString(KEY_TOKEN, null);
    }

    /** Recupera el nombre de usuario del login */
    public static String getUsername(Context ctx) {
        return prefs(ctx).getString(KEY_USERNAME, null);
    }

    /** Recupera roles */
    public static List<String> getAuthorities(Context ctx) {
        String csv = prefs(ctx).getString(KEY_AUTHORITIES, "");
        if (csv.isEmpty()) return Collections.emptyList();
        return Arrays.asList(csv.split(","));
    }

    /** Recupera el ID de usuario */
    public static int getUserId(Context ctx) {
        return prefs(ctx).getInt(KEY_USER_ID, -1);
    }

    /** ¿Es administrador? */
    public static boolean isAdmin(Context ctx) {
        for (String role : getAuthorities(ctx)) {
            if ("ROLE_ADMIN".equals(role) || "ADMIN".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /** Borra TODO (sesión + credenciales) */
    public static void clearSession(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }
    // Solo borra la sesión, pero conserva las credenciales recordadas
    public static void clearLoginOnly(Context ctx) {
        prefs(ctx).edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USERNAME)
                .remove(KEY_AUTHORITIES)
                .remove(KEY_USER_ID)
                .apply();
    }

    // ————— Métodos nuevos para “Recordar credenciales” —————

    /** Guarda usuario y contraseña en prefs */
    public static void saveCredentials(Context ctx, String user, String pass) {
        prefs(ctx).edit()
                .putString(KEY_SAVED_USER, user)
                .putString(KEY_SAVED_PASS, pass)
                .apply();
    }

    /** Borra sólo las credenciales guardadas */
    public static void clearCredentials(Context ctx) {
        prefs(ctx).edit()
                .remove(KEY_SAVED_USER)
                .remove(KEY_SAVED_PASS)
                .apply();
    }

    /** Recupera el usuario “recordado”, o null si no hay */
    public static String getSavedUsername(Context ctx) {
        return prefs(ctx).getString(KEY_SAVED_USER, null);
    }

    /** Recupera la contraseña “recordada”, o null si no hay */
    public static String getSavedPassword(Context ctx) {
        return prefs(ctx).getString(KEY_SAVED_PASS, null);
    }
    // Método para obtener el correo del usuario guardado en las preferencias
    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, "");  // Devuelve el correo almacenado, o un valor por defecto
    }
    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }

}