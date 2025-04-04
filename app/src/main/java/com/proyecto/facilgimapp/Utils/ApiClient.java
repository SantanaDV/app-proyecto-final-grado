package com.proyecto.facilgimapp.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase que representa el cliente Retrofit para interactuar con la API.
 * Esta clase se utiliza para configurar y crear una instancia de Retrofit
 * que se puede utilizar para realizar solicitudes HTTP a la API
 * mediante el patron de diseño singleton (Solo creamos una unica instancia durante la ejecución del programa.
 */
public class ApiClient {

    // Reemplazar con la URL base la api (actualmente  maquina local)
    private static final String BASE_URL = "http://10.110.4.196:8080/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //Mediante este metodo convertimos datos de JSON a objetos Java utilizando la biblioteca Gson
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}