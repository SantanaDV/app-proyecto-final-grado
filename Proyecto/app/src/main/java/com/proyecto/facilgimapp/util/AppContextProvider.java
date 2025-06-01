package com.proyecto.facilgimapp.util;

import android.content.Context;

/**
 * Proveedor global de contexto de Activity para uso en utilidades o clases
 * que requieran un Context fuera de componentes Android convencionales.
 * <p>
 * Implementa un patrón singleton para almacenar y recuperar el Context
 * de la Activity que esté activa en un dado momento. Es responsabilidad
 * del usuario llamar a {@link #set(Context)} cuando la Activity se inicia
 * y a {@link #clear()} cuando se destruye, para evitar fugas de memoria.
 * </p>
 *
 * @author Francisco Santana
 */
public class AppContextProvider {
    /**
     * Contexto de la Activity actualmente activa. Puede ser null si no se ha establecido.
     */
    private static Context activityContext;

    /**
     * Establece el Context de la Activity actual. Debe llamarse, por ejemplo, en
     * {@code onCreate()} de cada Activity para registrar su Context.
     *
     * @param context Context de la Activity que se desea almacenar.
     */
    public static void set(Context context) {
        activityContext = context;
    }

    /**
     * Obtiene el Context de la Activity almacenado. Puede devolver null si nunca
     * se ha llamado a {@link #set(Context)} o si se ha llamado a {@link #clear()}.
     *
     * @return Context registrado de la Activity o null si no está disponible.
     */
    public static Context get() {
        return activityContext;
    }

    /**
     * Limpia el Context almacenado, estableciéndolo a null. Se recomienda llamar
     * a este método en el {@code onDestroy()} de la Activity para evitar fugas
     * de memoria al mantener referencias a Context.
     */
    public static void clear() {
        activityContext = null;
    }
}
