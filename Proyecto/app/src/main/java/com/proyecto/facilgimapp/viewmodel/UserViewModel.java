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

/**
 * ViewModel encargado de gestionar las operaciones relacionadas con usuarios:
 * cargar la lista, crear, actualizar, eliminar usuarios y cambiar contraseñas.
 * <p>
 * Expone LiveData para:
 * <ul>
 *   <li>Lista de usuarios.</li>
 *   <li>Resultado de validación de la contraseña actual.</li>
 *   <li>Resultado de cambio de contraseña.</li>
 *   <li>Indicador de éxito o error en operaciones CRUD de usuario.</li>
 * </ul>
 * </p>
 *
 * Autor: Francisco Santana
 */
public class UserViewModel extends AndroidViewModel {
    /**
     * Repositorio que realiza llamadas a la API REST para operaciones de usuario.
     */
    private final UserRepository repository;

    /**
     * LiveData que contiene la lista de {@link UsuarioDTO} obtenida del servidor.
     */
    private final MutableLiveData<List<UsuarioDTO>> _usuarios = new MutableLiveData<>();
    /**
     * LiveData público para observar la lista de usuarios.
     *
     * @return LiveData con List<UsuarioDTO>.
     */
    public LiveData<List<UsuarioDTO>> getUsers() {
        return _usuarios;
    }

    /**
     * LiveData que indica si la contraseña actual es válida.
     * <p>
     * Se utiliza al cambiar contraseña para validar la contraseña anterior.
     * </p>
     */
    private final MutableLiveData<Boolean> _currentValid = new MutableLiveData<>();
    /**
     * LiveData público para observar el resultado de la validación de la contraseña actual.
     *
     * @return LiveData con Boolean: true si es válida, false en caso contrario.
     */
    public LiveData<Boolean> currentValid() {
        return _currentValid;
    }

    /**
     * LiveData que indica si el cambio de contraseña se realizó correctamente.
     */
    private final MutableLiveData<Boolean> _changed = new MutableLiveData<>();
    /**
     * LiveData público para observar el resultado del cambio de contraseña.
     *
     * @return LiveData con Boolean: true si se cambió la contraseña, false en caso contrario.
     */
    public LiveData<Boolean> changed() {
        return _changed;
    }

    /**
     * LiveData que indica si la última operación CRUD (crear, editar o borrar usuario)
     * fue exitosa.
     */
    private final MutableLiveData<Boolean> _opSuccess = new MutableLiveData<>();
    /**
     * LiveData público para observar el indicador de éxito de la última operación.
     *
     * @return LiveData con Boolean: true si la operación fue exitosa, false en caso contrario.
     */
    public LiveData<Boolean> opSuccess() {
        return _opSuccess;
    }

    /**
     * LiveData que contiene el mensaje de error de la última operación, si la hubo.
     */
    private final MutableLiveData<String> _opError = new MutableLiveData<>();
    /**
     * LiveData público para observar el mensaje de error de la última operación.
     *
     * @return LiveData con String que describe el error.
     */
    public LiveData<String> opError() {
        return _opError;
    }

    /**
     * Constructor que inicializa el repositorio con el contexto de la aplicación.
     *
     * @param application Aplicación actual, usada para obtener el contexto.
     */
    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application.getApplicationContext());
    }

    /**
     * Carga la lista completa de usuarios desde el servidor.
     * <p>
     * Al recibir la respuesta, actualiza {@link #_usuarios} con la lista. En caso
     * de fallo, establece el valor en null.
     * </p>
     */
    public void loadUsers() {
        repository.listUsers().enqueue(new Callback<List<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<List<UsuarioDTO>> call,
                                   Response<List<UsuarioDTO>> response) {
                if (response.isSuccessful()) {
                    _usuarios.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<UsuarioDTO>> call, Throwable t) {
                _usuarios.setValue(null);
            }
        });
    }

    /**
     * Crea un nuevo usuario en el servidor.
     * <p>
     * Si la creación es exitosa, actualiza {@link #_opSuccess} a true y recarga
     * la lista de usuarios. Si falla, extrae el mensaje de error del cuerpo de respuesta
     * o usa un mensaje por defecto, publica en {@link #_opError} y establece {@link #_opSuccess} a false.
     * </p>
     *
     * @param user DTO con los datos del nuevo usuario a crear.
     */
    public void addUser(UsuarioDTO user) {
        repository.createUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    String msg = getApplication().getString(R.string.error_create_user);
                    ResponseBody err = response.errorBody();
                    try {
                        if (err != null) msg = err.string();
                    } catch (IOException ignored) {
                    }
                    _opError.setValue(msg);
                    _opSuccess.setValue(false);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _opError.setValue(getApplication().getString(R.string.fallo_red) + ": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /**
     * Actualiza un usuario existente en el servidor.
     * <p>
     * Si la actualización es exitosa, establece {@link #_opSuccess} a true y recarga
     * la lista de usuarios. Si falla, publica un mensaje de error genérico en {@link #_opError}
     * y establece {@link #_opSuccess} a false.
     * </p>
     *
     * @param id  ID del usuario a actualizar.
     * @param dto DTO con los datos actualizados del usuario.
     */
    public void updateUser(int id, UsuarioDTO dto) {
        repository.updateUser(id, dto).enqueue(new Callback<UsuarioDTO>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call,
                                   Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    _opError.setValue(getApplication().getString(R.string.error_guardar_cambios));
                    _opSuccess.setValue(false);
                }
            }
            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                _opError.setValue(getApplication().getString(R.string.fallo_red) + ": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /**
     * Elimina un usuario en el servidor.
     * <p>
     * Si la eliminación es exitosa, establece {@link #_opSuccess} a true y recarga
     * la lista de usuarios. Si falla, publica un mensaje de error genérico en {@link #_opError}
     * y establece {@link #_opSuccess} a false.
     * </p>
     *
     * @param id ID del usuario a eliminar.
     */
    public void deleteUser(int id) {
        repository.deleteUser(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call,
                                   Response<Void> response) {
                if (response.isSuccessful()) {
                    _opSuccess.setValue(true);
                    loadUsers();
                } else {
                    _opError.setValue(getApplication().getString(R.string.error_eliminar_usuario));
                    _opSuccess.setValue(false);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _opError.setValue(getApplication().getString(R.string.fallo_red) + ": " + t.getMessage());
                _opSuccess.setValue(false);
            }
        });
    }

    /**
     * Valida la contraseña actual del usuario ante el backend.
     * <p>
     * Si la validación es exitosa (HTTP 200), establece {@link #_currentValid} a true,
     * en caso contrario a false. En fallos de red, también establece false.
     * </p>
     *
     * @param username Nombre de usuario cuya contraseña se valida.
     * @param current  Cadena con la contraseña actual a verificar.
     */
    public void validateCurrentPassword(String username, String current) {
        repository.validateCurrentPassword(username, current)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call,
                                           Response<Void> response) {
                        _currentValid.setValue(response.isSuccessful());
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        _currentValid.setValue(false);
                    }
                });
    }

    /**
     * Cambia la contraseña del usuario especificado en el backend.
     * <p>
     * Si el cambio es exitoso (HTTP 200), establece {@link #_changed} a true,
     * en caso contrario a false. En fallos de red, también establece false.
     * </p>
     *
     * @param userId ID del usuario cuya contraseña se cambia.
     * @param newPwd Nueva contraseña a asignar.
     */
    public void changePassword(int userId, String newPwd) {
        repository.changePassword(userId, newPwd)
                .enqueue(new Callback<UsuarioDTO>() {
                    @Override
                    public void onResponse(Call<UsuarioDTO> call,
                                           Response<UsuarioDTO> response) {
                        _changed.setValue(response.isSuccessful());
                    }
                    @Override
                    public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                        _changed.setValue(false);
                    }
                });
    }
}
