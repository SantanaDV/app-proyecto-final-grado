package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.LoginRequest;
import com.proyecto.facilgimapp.model.dto.LoginResponse;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.repository.AuthRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;


    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    // Para errores de registro
    private final MutableLiveData<String> registerError = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application app) {
        super(app);
        repository = new AuthRepository(app.getApplicationContext());
    }

    /** Observa el resultado del login: éxito = LoginResponse, fallo =  null */
    public LiveData<LoginResponse> login(LoginRequest req) {
        repository.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                loginResult.setValue(resp.isSuccessful() ? resp.body() : null);
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.setValue(null);
            }
        });
        return loginResult;
    }

    /**
     * Observa el resultado del registro:
     * – Devuelve un LiveData NUEVO por llamada, que emitirá el UsuarioDTO en caso de éxito o null en fallo.
     * – En caso de error (400, 409, etc) extrae el mensaje y lo publica en registerError.
     */
    public LiveData<UsuarioDTO> register(UsuarioRequestDTO dto) {
        // Limpiamos cualquier error previo
        registerError.setValue(null);

        // Creamos un LiveData nuevo para este registro
        MutableLiveData<UsuarioDTO> result = new MutableLiveData<>();

        repository.registerUser(dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> resp) {
                if (resp.isSuccessful()) {
                    // Registro OK, entonces emitimos el cuerpo
                    result.setValue(resp.body());
                } else {
                    // Registro FALLIDO, entonces extraemos mensaje del errorBody
                    String msg;
                    try {
                        msg = (resp.errorBody() != null)
                                ? resp.errorBody().string()
                                : String.valueOf(R.string.error_unknown);
                    } catch (IOException e) {
                        msg = String.valueOf(R.string.error_unknown);
                    }
                    registerError.setValue(msg);
                    result.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                // Error de red o excepción  lo mostramos tambien
                registerError.setValue(t.getMessage());
                result.setValue(null);
            }
        });

        return result;
    }

    /** Para que el Fragment observe posibles mensajes de error del registro */
    public LiveData<String> getRegisterError() {
        return registerError;
    }
}
