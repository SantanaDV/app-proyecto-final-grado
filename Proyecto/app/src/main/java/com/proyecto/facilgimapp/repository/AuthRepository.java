package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.LoginRequest;
import com.proyecto.facilgimapp.model.dto.LoginResponse;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import retrofit2.Call;
/**
 * Clase responsable de gestionar las operaciones de autenticación contra el servidor,
 * tales como inicio de sesión y registro de usuarios. Utiliza Retrofit para comunicarse
 * con la API REST.
 * 
 * @autor: Francisco Santana
 */
public class AuthRepository {
    private final ApiService apiService;
    /**
     * Constructor que inicializa el servicio de API obteniéndolo de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public AuthRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }
    
    /**
     * Realiza una solicitud de inicio de sesión al servidor.
     *
     * @param request Objeto que contiene las credenciales del usuario (nombre de usuario y contraseña).
     * @return Un objeto Call que representa la solicitud de inicio de sesión.
     */
    public Call<LoginResponse> login(LoginRequest request) {
        return apiService.login(request);
    }
/**
     * Realiza la operación de registro de un nuevo usuario en el sistema.
     * <p>
     * Envía un objeto {@link UsuarioRequestDTO} con los datos necesarios para crear
     * el usuario y devuelve un objeto {@link Call<UsuarioDTO>} que contendrá la respuesta
     * con los datos del usuario registrado.
     *
     * @param dto Objeto que contiene la información del usuario a registrar.
     * @return {@link Call} con la respuesta de tipo {@link UsuarioDTO}.
     */
    public Call<UsuarioDTO> registerUser(UsuarioRequestDTO dto) {
        return apiService.registerUser(dto);
    }
}
