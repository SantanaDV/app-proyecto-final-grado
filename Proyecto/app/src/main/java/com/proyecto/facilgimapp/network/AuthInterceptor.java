package com.proyecto.facilgimapp.network;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.proyecto.facilgimapp.util.SessionManager;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

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
