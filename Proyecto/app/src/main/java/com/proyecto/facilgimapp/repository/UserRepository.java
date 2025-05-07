package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<List<UsuarioDTO>> listUsers() {
        return apiService.listUsers();
    }

    public Call<String> createUser(UsuarioDTO usuario) {
        return apiService.createUser(usuario);
    }

    public Call<String> deleteUser(int userId) {
        return apiService.deleteUser(userId);
    }
}
