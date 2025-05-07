package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.LoginRequest;
import com.proyecto.facilgimapp.model.LoginResponse;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import retrofit2.Call;

public class AuthRepository {
    private final ApiService apiService;

    public AuthRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return apiService.login(request);
    }

    public Call<UsuarioDTO> registerUser(UsuarioRequestDTO dto) {
        return apiService.registerUser(dto);
    }
}
