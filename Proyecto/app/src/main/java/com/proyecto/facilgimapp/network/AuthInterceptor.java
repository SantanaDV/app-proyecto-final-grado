package com.proyecto.facilgimapp.network;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.proyecto.facilgimapp.util.SessionManager;

    /**
     * Interceptor de autenticación para añadir el token JWT a las peticiones HTTP.
     * <p>
     * Esta clase intercepta las solicitudes salientes y, si el usuario está autenticado,
     * añade el encabezado "Authorization" con el token Bearer a todas las peticiones,
     * excepto a las rutas de login y registro de usuario.
     * </p>
     *
     * @author Francisco Santana
     */
     
public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Intercepta la solicitud HTTP y añade el encabezado de autorización si corresponde.
     *
     * @param chain La cadena de interceptores que maneja la solicitud.
     * @return La respuesta HTTP, posiblemente modificada con el encabezado de autorización.
     * @throws IOException Si ocurre un error de entrada/salida durante la solicitud.
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = SessionManager.getToken(context);
        if (token != null && !original.url().encodedPath().endsWith("/login")
                && !original.url().encodedPath().endsWith("/usuarios/registrar")) {
            Request authorised = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authorised);
        }
        return chain.proceed(original);
    }
}
