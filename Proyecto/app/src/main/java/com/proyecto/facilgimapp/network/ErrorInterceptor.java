package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.proyecto.facilgimapp.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorInterceptor implements Interceptor {

    private final Context appContext;

    public ErrorInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            // Si es la validación de contraseña, NO forzamos logout:
            String path = request.url().encodedPath();
            if (path.endsWith("/validate-password")) {
                // devolvemos el 401 para que tu callback lo maneje
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
