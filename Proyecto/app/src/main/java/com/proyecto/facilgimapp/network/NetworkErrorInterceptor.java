package com.proyecto.facilgimapp.network;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Interceptor de OkHttp que marca la conexión como DOWN ante IOException
 * y vuelve a UP si la llamada fue bien.
 * Esta clase se utiliza para detectar problemas de red
 * y actualizar el estado de la conexión en la aplicación.
 *  @author Francisco Santana
 */
public class NetworkErrorInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Response resp = chain.proceed(chain.request());
            // Si queremos también detectar códigos >= 500
             if (resp.code() >= 500) ConnectionState.get().postDown();
            ConnectionState.get().postUp();
            return resp;
        } catch (IOException e) {
            ConnectionState.get().postDown();
            throw e;
        }
    }
}
