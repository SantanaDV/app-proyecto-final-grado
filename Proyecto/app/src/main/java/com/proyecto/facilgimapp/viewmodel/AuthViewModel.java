package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.LoginRequest;
import com.proyecto.facilgimapp.model.dto.LoginResponse;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<UsuarioDTO> registerResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application app) {
        super(app);
        repository = new AuthRepository(app.getApplicationContext());
    }

    public LiveData<LoginResponse> login(LoginRequest req) {
        repository.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> c, Response<LoginResponse> r) {
                loginResult.setValue(r.isSuccessful() ? r.body() : null);
            }
            @Override public void onFailure(Call<LoginResponse> c, Throwable t) {
                loginResult.setValue(null);
            }
        });
        return loginResult;
    }

    public LiveData<UsuarioDTO> register(UsuarioRequestDTO dto) {
        repository.registerUser(dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> c, Response<UsuarioDTO> r) {
                registerResult.setValue(r.isSuccessful() ? r.body() : null);
            }
            @Override public void onFailure(Call<UsuarioDTO> c, Throwable t) {
                registerResult.setValue(null);
            }
        });
        return registerResult;
    }
}
