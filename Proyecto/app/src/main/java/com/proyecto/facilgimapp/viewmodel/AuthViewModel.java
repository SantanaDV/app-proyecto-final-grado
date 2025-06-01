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

/**
 * ViewModel encargado de manejar las operaciones de autenticación:
 * login y registro de usuarios mediante {@link AuthRepository}.
 * <p>
 * Expondrá {@link LiveData} para que la UI observe el resultado del login
 * y posibles errores de registro.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;

    /**
     * LiveData que emitirá el resultado del login: un {@link LoginResponse}
     * si fue exitoso o null en caso de fallo.
     */
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    /**
     * LiveData que emitirá un mensaje de error en el registro, o null
     * si no hay error. Se actualiza cuando ocurre un error HTTP o de red.
     */
    private final MutableLiveData<String> registerError = new MutableLiveData<>();

    /**
     * Constructor que inicializa el repositorio de autenticación.
     *
     * @param app La aplicación, usada para obtener el contexto necesario.
     */
    public AuthViewModel(@NonNull Application app) {
        super(app);
        repository = new AuthRepository(app.getApplicationContext());
    }

    /**
     * Realiza la operación de login con los datos proporcionados en {@link LoginRequest}.
     * <p>
     * Se envía la petición al repositorio y se publica el {@link LoginResponse} en
     * {@link #loginResult} si la llamada fue exitosa, o null en caso de fallo.
     * </p>
     *
     * @param req DTO con usuario y contraseña para el login.
     * @return {@link LiveData} que emitirá el {@link LoginResponse} o null.
     */
    public LiveData<LoginResponse> login(LoginRequest req) {
        repository.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                loginResult.setValue(resp.isSuccessful() ? resp.body() : null);
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.setValue(null);
            }
        });
        return loginResult;
    }

    /**
     * Realiza la operación de registro de un nuevo usuario usando {@link UsuarioRequestDTO}.
     * <p>
     * Crea un {@link MutableLiveData<UsuarioDTO>} para esta llamada y:
     * <ul>
     *   <li>Si la respuesta HTTP es exitosa, emite el {@link UsuarioDTO} recibido.</li>
     *   <li>Si el servidor devuelve un error (códigos 400, 409, etc.), extrae el mensaje
     *       del cuerpo de error y lo publica en {@link #registerError}, emitiendo null.</li>
     *   <li>En caso de fallo de red o excepción, publica el mensaje de excepción en
     *       {@link #registerError} y emite null.</li>
     * </ul>
     * </p>
     *
     * @param dto DTO con los datos del nuevo usuario a registrar.
     * @return {@link LiveData} que emitirá el {@link UsuarioDTO} en caso de registro exitoso, o null en caso de fallo.
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
                    // Registro exitoso: emitimos el usuario
                    result.setValue(resp.body());
                } else {
                    // Registro fallido: extraemos mensaje de error
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
                // Error de red o excepción: publicamos el mensaje
                registerError.setValue(t.getMessage());
                result.setValue(null);
            }
        });

        return result;
    }

    /**
     * Permite que la UI observe mensajes de error que ocurran durante el registro.
     *
     * @return {@link LiveData<String>} con el mensaje de error o null si no hay.
     */
    public LiveData<String> getRegisterError() {
        return registerError;
    }
}
