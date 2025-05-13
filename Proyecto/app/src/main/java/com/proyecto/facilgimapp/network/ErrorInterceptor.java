package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.util.AppContextProvider;
import com.proyecto.facilgimapp.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorInterceptor implements Interceptor {

    private final Context appContext;

    public ErrorInterceptor(Context context) {
        // Usamos el contexto de aplicación para evitar leaks
        this.appContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            // Token expirado o inválido
            Context activityContext = AppContextProvider.get();
            if (activityContext != null) {
                SessionManager.clearSession(activityContext);
                new Handler(Looper.getMainLooper()).post(() -> {
                    SessionRedirector.redirectToLogin(activityContext);
                });
            }
        }

        return response;
    }
}
