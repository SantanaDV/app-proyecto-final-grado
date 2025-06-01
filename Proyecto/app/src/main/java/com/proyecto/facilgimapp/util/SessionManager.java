package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.proyecto.facilgimapp.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Gestor de sesión y credenciales de usuario, almacenadas en SharedPreferences.
 * <p>
 * Permite guardar y recuperar datos de autenticación (token JWT, nombre de usuario,
 * roles e ID), así como credenciales para “recordar usuario y contraseña” y correo.
 * Proporciona métodos para limpiar la sesión o sólo las credenciales.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class SessionManager {
    private static final String PREFS_NAME       = "facilgim_session";
    private static final String KEY_TOKEN        = "key_token";
    private static final String KEY_USERNAME     = "key_username";
    private static final String KEY_AUTHORITIES  = "key_authorities";
    private static final String KEY_USER_ID      = "key_user_id";
    private static final String KEY_SAVED_USER   = "key_saved_user";
    private static final String KEY_SAVED_PASS   = "key_saved_pass";
    private static final String KEY_USER_EMAIL   = "key_user_email";

    /**
     * Obtiene la instancia de SharedPreferences con nombre {@link #PREFS_NAME} en modo privado.
     *
     * @param ctx Contexto de la aplicación.
     * @return Objeto {@link SharedPreferences} asociado a la sesión.
     */
    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Guarda datos de inicio de sesión: token JWT, nombre de usuario, lista de roles e ID de usuario.
     *
     * @param ctx         Contexto de la aplicación.
     * @param token       Token JWT de autenticación.
     * @param username    Nombre de usuario.
     * @param authorities Lista de roles (por ejemplo, ["ROLE_USER", "ROLE_ADMIN"]).
     * @param userId      Identificador numérico del usuario.
     */
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

    /**
     * Recupera el token JWT almacenado en preferencias.
     *
     * @param ctx Contexto de la aplicación.
     * @return Token JWT o null si no existe.
     */
    public static String getToken(Context ctx) {
        return prefs(ctx).getString(KEY_TOKEN, null);
    }

    /**
     * Recupera el nombre de usuario almacenado en preferencias.
     *
     * @param ctx Contexto de la aplicación.
     * @return Nombre de usuario o null si no existe.
     */
    public static String getUsername(Context ctx) {
        return prefs(ctx).getString(KEY_USERNAME, null);
    }

    /**
     * Recupera la lista de roles (authorities) almacenada en preferencias.
     *
     * @param ctx Contexto de la aplicación.
     * @return Lista de cadenas con cada rol; devuelve lista vacía si no hay roles.
     */
    public static List<String> getAuthorities(Context ctx) {
        String csv = prefs(ctx).getString(KEY_AUTHORITIES, "");
        if (csv.isEmpty()) return Collections.emptyList();
        return Arrays.asList(csv.split(","));
    }

    /**
     * Recupera el ID de usuario almacenado en preferencias.
     *
     * @param ctx Contexto de la aplicación.
     * @return Entero con el ID de usuario, o -1 si no existe.
     */
    public static int getUserId(Context ctx) {
        return prefs(ctx).getInt(KEY_USER_ID, -1);
    }

    /**
     * Comprueba si el usuario tiene rol de administrador ("ROLE_ADMIN" o "ADMIN").
     *
     * @param ctx Contexto de la aplicación.
     * @return {@code true} si entre las autoridades se encuentra un rol de administrador.
     */
    public static boolean isAdmin(Context ctx) {
        for (String role : getAuthorities(ctx)) {
            if ("ROLE_ADMIN".equals(role) || "ADMIN".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina todos los datos de sesión y credenciales almacenados.
     * <p>
     * Equivale a llamar a {@link SharedPreferences.Editor#clear} y aplicar.
     * </p>
     *
     * @param ctx Contexto de la aplicación.
     */
    public static void clearSession(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    /**
     * Elimina sólo los datos de sesión (token, usuario, roles e ID),
     * pero conserva las credenciales (“recordar usuario/contraseña”).
     *
     * @param ctx Contexto de la aplicación.
     */
    public static void clearLoginOnly(Context ctx) {
        prefs(ctx).edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USERNAME)
                .remove(KEY_AUTHORITIES)
                .remove(KEY_USER_ID)
                .apply();
    }

    // ————— Métodos para “Recordar credenciales” —————

    /**
     * Guarda las credenciales para “recordar” usuario y contraseña.
     *
     * @param ctx  Contexto de la aplicación.
     * @param user Cadena con el nombre de usuario.
     * @param pass Cadena con la contraseña.
     */
    public static void saveCredentials(Context ctx, String user, String pass) {
        prefs(ctx).edit()
                .putString(KEY_SAVED_USER, user)
                .putString(KEY_SAVED_PASS, pass)
                .apply();
    }

    /**
     * Borra sólo las credenciales guardadas (usuario y contraseña),
     * sin afectar el resto de datos de sesión.
     *
     * @param ctx Contexto de la aplicación.
     */
    public static void clearCredentials(Context ctx) {
        prefs(ctx).edit()
                .remove(KEY_SAVED_USER)
                .remove(KEY_SAVED_PASS)
                .apply();
    }

    /**
     * Recupera el usuario “recordado” (guardado previamente).
     *
     * @param ctx Contexto de la aplicación.
     * @return Cadena con el nombre de usuario o null si no existe.
     */
    public static String getSavedUsername(Context ctx) {
        return prefs(ctx).getString(KEY_SAVED_USER, null);
    }

    /**
     * Recupera la contraseña “recordada” (guardada previamente).
     *
     * @param ctx Contexto de la aplicación.
     * @return Cadena con la contraseña o null si no existe.
     */
    public static String getSavedPassword(Context ctx) {
        return prefs(ctx).getString(KEY_SAVED_PASS, null);
    }

    /**
     * Guarda el correo electrónico del usuario en preferencias.
     *
     * @param context Contexto de la aplicación.
     * @param email   Cadena con el correo electrónico a almacenar.
     */
    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    /**
     * Recupera el correo electrónico del usuario guardado.
     *
     * @param context Contexto de la aplicación.
     * @return Cadena con el correo electrónico o cadena vacía si no existe.
     */
    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Comprueba si hay un usuario actualmente autenticado (token no nulo ni vacío).
     *
     * @param context Contexto de la aplicación.
     * @return {@code true} si existe un token válido, {@code false} en caso contrario.
     */
    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isEmpty();
    }
}
