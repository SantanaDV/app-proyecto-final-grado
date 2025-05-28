package com.proyecto.facilgimapp.repository;

import android.content.Context;

import com.proyecto.facilgimapp.model.dto.PasswordDTO;
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

    // Métodos existentes
    public Call<List<UsuarioDTO>> listUsers() {
        return apiService.listUsers();
    }

    public Call<Void> createUser(UsuarioDTO usuario) {
        return apiService.createUser(usuario);
    }

    public Call<Void> deleteUser(int userId) {
        return apiService.deleteUser(userId);
    }

    public Call<UsuarioDTO> updateUser(int id, UsuarioDTO dto) {
        return apiService.updateUser(id, dto);
    }

    // Métodos nuevos para actualizar nombre y contraseña
    public Call<UsuarioDTO> updateUsername(int id, String newUsername) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(id);
        dto.setUsername(newUsername);
        return apiService.updateUser(id, dto);
    }

    public Call<UsuarioDTO> updatePassword(int id, String newPassword) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(id);
        dto.setPassword(newPassword);
        return apiService.updateUser(id, dto);
    }

    public Call<Void> validateCurrentPassword(String username, String password) {
        return apiService.validateCurrentPassword(username, new PasswordDTO(password));
    }

    public Call<UsuarioDTO> changePassword(int userId, String newPassword) {
        return apiService.changePassword(userId, new PasswordDTO(newPassword));
    }

    public Call<UsuarioDTO> updatePasswordById(int id, String newPassword) {
        PasswordDTO dto = new PasswordDTO();
        dto.setPassword(newPassword);
        return apiService.updatePassword(id, dto);
    }

}
