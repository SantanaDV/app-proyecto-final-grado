package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;

    // Lista de usuarios
    private final MutableLiveData<List<UsuarioDTO>> _usuarios = new MutableLiveData<>();
    public LiveData<List<UsuarioDTO>> getUsers() { return _usuarios; }

    // Resultado de validar la contraseña actual
    private final MutableLiveData<Boolean> _currentValid = new MutableLiveData<>();
    public LiveData<Boolean> currentValid() { return _currentValid; }

    // Resultado de cambiar la contraseña
    private final MutableLiveData<Boolean> _changed = new MutableLiveData<>();
    public LiveData<Boolean> changed() { return _changed; }

    // Indicador de operación exitosa (crear/editar/borrar)
    private final MutableLiveData<Boolean> _opSuccess = new MutableLiveData<>();
    public LiveData<Boolean> opSuccess() { return _opSuccess; }

    // Mensaje de error de operación
    private final MutableLiveData<String> _opError = new MutableLiveData<>();
    public LiveData<String> opError() { return _opError; }

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application.getApplicationContext());
    }

    /** Carga la lista completa de usuarios */
    public void loadUsers() {
        repository.listUsers().enqueue(new Callback<List<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<List<UsuarioDTO>> call,
                                   Response<List<UsuarioDTO>> response) {
                if (response.isSuccessful()) {
                    _usuarios.setValue(response.body());
                }
            }
            @Override public void onFailure(Call<List<UsuarioDTO>> call, Throwable t) {
                _usuarios.setValue(null);
            }
        });
    }

    /** Crea un nuevo usuario y recarga la lista */
    public void addUser(UsuarioDTO user) {
        repository.createUser(user).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    String msg = getApplication().getString(R.string.error_create_user);
                    ResponseBody err = response.errorBody();
                    try { if (err != null) msg = err.string(); }
                    catch (IOException ignored) { }
                    _opError.setValue(msg);
                    _opSuccess.setValue(false);
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                _opError.setValue( R.string.fallo_red+": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /** Actualiza un usuario existente y recarga la lista */
    public void updateUser(int id, UsuarioDTO dto) {
        repository.updateUser(id, dto).enqueue(new Callback<UsuarioDTO>() {
            @Override public void onResponse(Call<UsuarioDTO> call,
                                             Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    _opError.setValue(getApplication().getString(R.string.error_guardar_cambios));
                    _opSuccess.setValue(false);
                }
            }
            @Override public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                _opError.setValue(R.string.fallo_red+": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /** Elimina un usuario y recarga la lista */
    public void deleteUser(int id) {
        repository.deleteUser(id).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call,
                                             Response<Void> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    _opError.setValue(getApplication().getString(R.string.error_eliminar_usuario));
                    _opSuccess.setValue(false);
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                _opError.setValue(R.string.fallo_red+": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /** Valida la contraseña actual del usuario */
    public void validateCurrentPassword(String username, String current) {
        repository.validateCurrentPassword(username, current)
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call,
                                                     Response<Void> response) {
                        _currentValid.setValue(response.isSuccessful());
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        _currentValid.setValue(false);
                    }
                });
    }

    /** Cambia la contraseña del usuario */
    public void changePassword(int userId, String newPwd) {
        repository.changePassword(userId, newPwd)
                .enqueue(new Callback<UsuarioDTO>() {
                    @Override public void onResponse(Call<UsuarioDTO> call,
                                                     Response<UsuarioDTO> response) {
                        _changed.setValue(response.isSuccessful());
                    }
                    @Override public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                        _changed.setValue(false);
                    }
                });
    }
}
