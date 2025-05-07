package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.repository.UserRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<UsuarioDTO>> usuarios = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application.getApplicationContext());
    }

    public MutableLiveData<List<UsuarioDTO>> getUsers() {
        return usuarios;
    }

    public void loadUsers() {
        repository.listUsers().enqueue(new Callback<List<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<List<UsuarioDTO>> call,
                                   Response<List<UsuarioDTO>> response) {
                if (response.isSuccessful()) {
                    usuarios.setValue(response.body());
                }
            }
            @Override public void onFailure(Call<List<UsuarioDTO>> call, Throwable t) { }
        });
    }

    public void addUser(UsuarioDTO user) {
        repository.createUser(user).enqueue(new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) loadUsers();
            }
            @Override public void onFailure(Call<String> call, Throwable t) { }
        });
    }

    public void deleteUser(int id) {
        repository.deleteUser(id).enqueue(new Callback<String>() {
            @Override public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) loadUsers();
            }
            @Override public void onFailure(Call<String> call, Throwable t) { }
        });
    }
}
