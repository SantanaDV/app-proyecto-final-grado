package com.proyecto.facilgimapp.util;

import android.content.Context;

/**
 * Clase singleton para almacenar el contexto de la activity actual.
 */
public class AppContextProvider {
    private static Context activityContext;

    public static void set(Context context) {
        activityContext = context;
    }

    public static Context get() {
        return activityContext;
    }

    public static void clear() {
        activityContext = null;
    }
}
