package com.proyecto.facilgimapp.network;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proyecto.facilgimapp.util.LocalDateAdapter;
import java.time.LocalDate;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://10.0.2.2:8443/";
    //Para pruebas en el movil
    //private static final String BASE_URL = "https://10.110.4.196:8443/";
    private static Retrofit retrofit;

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            // 1) Configurar GSON para (de)serializar LocalDate
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            // 2) Crear el interceptor de logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                    android.util.Log.d("OKHTTP", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 3) Construir el OkHttpClient con interceptores
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(logging);

            /* ---------------------------------------------------------
             * BLOQUE DE DESARROLLO: hostnameVerifier permisivo
             * Permite que OkHttp acepte el certificado con CN=localhost
             * cuando el hostname es 10.0.2.2 o localhost.
             * Quitar este bloque en producción y usar un cert con SAN.
             */
            clientBuilder.hostnameVerifier((hostname, session) ->
                    "10.0.2.2".equals(hostname)
                            || "localhost".equals(hostname)
                            || "10.110.4.196".equals(hostname)
            );
            /* --------------------------------------------------------- */

            OkHttpClient client = clientBuilder.build();

            // 4) Crear Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
