package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.proyecto.facilgimapp.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Interceptor de errores para las peticiones de red usando OkHttp.
 * <p>
 * Este interceptor se encarga de manejar respuestas HTTP 401 (no autorizado).
 * Si la respuesta corresponde a una validación de contraseña, permite que el callback lo maneje.
 * En cualquier otro caso, limpia la sesión del usuario y redirige a la pantalla de login.
 * </p>
 *
 * @author Francisco Santana
 */

public class ErrorInterceptor implements Interceptor {

    private final Context appContext;

     /**
     * Crea una nueva instancia de ErrorInterceptor.
     *
     * @param context Contexto de la aplicación, utilizado para limpiar la sesión y redirigir al login.
     */
    public ErrorInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }
    /**
     * Intercepta la petición y respuesta HTTP.
     * <p>
     * Si la respuesta es 401 y no corresponde a la validación de contraseña,
     * limpia la sesión y redirige al login.
     * </p>
     *
     * @param chain Cadena de interceptores de OkHttp.
     * @return La respuesta HTTP, posiblemente después de limpiar sesión y redirigir.
     * @throws IOException Si ocurre un error de entrada/salida durante la petición.
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            // Si es la validación de contraseña, NO forzamos logout:
            String path = request.url().encodedPath();
            if (path.endsWith("/validate-password")) {
                // devolvemos el 401 para que el callback lo maneje
                return response;
            }
            // en cualquier otro caso, sí limpiamos sesión y redirigimos:
            SessionManager.clearLoginOnly(appContext);
            new Handler(Looper.getMainLooper()).post(() -> {
                SessionRedirector.redirectToLogin(appContext);
            });
        }

        return response;
    }
}
