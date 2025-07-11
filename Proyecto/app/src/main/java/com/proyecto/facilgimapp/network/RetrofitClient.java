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
import com.proyecto.facilgimapp.BuildConfig;
/**
 * Clase responsable de configurar y proporcionar una instancia de Retrofit
 * para las llamadas a la API REST. Incluye la inicialización de GSON para 
 * serializar/deserializar objetos LocalDate y la configuración de los 
 * interceptores necesarios (autenticación, manejo de errores y logging).
 *
 * @author Francisco Santana
 */
public class RetrofitClient {
    private static final String BASE_URL = BuildConfig.BASE_URL ;
    //Este comando hay que hacerlo cada vez que enchufe el usb
    //comando para aceptar peticiones https en el dispositivo fisico: adb -s PVEM6DHELBNN5THQ reverse tcp:8443 tcp:8443
    private static Retrofit retrofit;

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            //  Configurar GSON para (de)serializar LocalDate
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            // Crear el interceptor de logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                    android.util.Log.d("OKHTTP", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Construir el OkHttpClient con interceptores
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(new ErrorInterceptor(context)) // añadimos el manejo de errores 401 sesion expirada
                    .addInterceptor(new NetworkErrorInterceptor())  // captura timeouts, IO, 5xx,etc..
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
                            || "10.110.4.43".equals(hostname)
                            || "192.168.0.195".equals(hostname)
                            || "91.99.49.236.sslip.io".equals(hostname)
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
