package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<UsuarioDTO>> _usuarios = new MutableLiveData<>();
    public LiveData<List<UsuarioDTO>> getUsers() {
        return _usuarios;
    }

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application.getApplicationContext());
    }

    public void loadUsers() {
        repository.listUsers().enqueue(new Callback<List<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<List<UsuarioDTO>> call, Response<List<UsuarioDTO>> response) {
                if (response.isSuccessful()) {
                    _usuarios.setValue(response.body());
                }
            }

            @Override public void onFailure(Call<List<UsuarioDTO>> call, Throwable t) {
                _usuarios.setValue(null);
            }
        });
    }

    public void addUser(UsuarioDTO user) {
        repository.createUser(user).enqueue(new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) loadUsers();
            }

            @Override public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void deleteUser(int id) {
        repository.deleteUser(id).enqueue(new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) loadUsers();
            }

            @Override public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void updateUser(int id, UsuarioDTO dto) {
        repository.updateUser(id, dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) loadUsers();
            }

            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                // Handle failure
            }
        });
    }

    // Método para actualizar el nombre de usuario
    public void updateUsername(int userId, String newUsername) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(userId);
        dto.setUsername(newUsername);

        repository.updateUser(userId, dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, actualizar la lista de usuarios
                    loadUsers();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                // Manejar errores
            }
        });
    }

    // Método para actualizar la contraseña
    public void updatePassword(int userId, String newPassword) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(userId);
        dto.setPassword(newPassword);  // Necesitas tener un campo de password en tu DTO

        repository.updateUser(userId, dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, actualizar la lista de usuarios
                    loadUsers();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                // Manejar errores
            }
        });
    }
}
