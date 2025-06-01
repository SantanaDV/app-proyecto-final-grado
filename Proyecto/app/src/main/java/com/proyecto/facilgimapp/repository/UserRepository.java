package com.proyecto.facilgimapp.repository;

import android.content.Context;

import com.proyecto.facilgimapp.model.dto.PasswordDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;

/**
 * Repositorio encargado de gestionar las operaciones CRUD de usuarios
 * contra la API REST mediante Retrofit. Incluye métodos para listar,
 * crear, actualizar y eliminar usuarios, así como para validar y cambiar contraseñas.
 *
 * @author Francisco Santana
 */
public class UserRepository {
    /**
     * Servicio de API proporcionado por Retrofit para realizar las llamadas HTTP.
     */
    private final ApiService apiService;

    /**
     * Constructor que inicializa el servicio de API obteniéndolo de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    /**
     * Recupera la lista completa de usuarios registrados.
     *
     * @return {@link Call} que, al ejecutarse, devuelve una lista de {@link UsuarioDTO}.
     */
    public Call<List<UsuarioDTO>> listUsers() {
        return apiService.listUsers();
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param usuario Objeto {@link UsuarioDTO} con los datos del usuario a crear.
     * @return {@link Call} que, al ejecutarse, no devuelve contenido (Void).
     */
    public Call<Void> createUser(UsuarioDTO usuario) {
        return apiService.createUser(usuario);
    }

    /**
     * Elimina un usuario por su identificador único.
     *
     * @param userId Identificador del usuario a eliminar.
     * @return {@link Call} que, al ejecutarse, no devuelve contenido (Void).
     */
    public Call<Void> deleteUser(int userId) {
        return apiService.deleteUser(userId);
    }

    /**
     * Actualiza un usuario existente utilizando todos los campos del DTO proporcionado.
     *
     * @param id  Identificador del usuario a actualizar.
     * @param dto {@link UsuarioDTO} que contiene los datos actualizados del usuario.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link UsuarioDTO} actualizado.
     */
    public Call<UsuarioDTO> updateUser(int id, UsuarioDTO dto) {
        return apiService.updateUser(id, dto);
    }

    /**
     * Actualiza únicamente el nombre de usuario (username) de un usuario existente.
     * Construye un {@link UsuarioDTO} parcial con el nuevo username.
     *
     * @param id          Identificador del usuario cuyo username se actualizará.
     * @param newUsername Nuevo nombre de usuario a asignar.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link UsuarioDTO} con el username actualizado.
     */
    public Call<UsuarioDTO> updateUsername(int id, String newUsername) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(id);
        dto.setUsername(newUsername);
        return apiService.updateUser(id, dto);
    }

    /**
     * Actualiza únicamente la contraseña de un usuario existente.
     * Construye un {@link UsuarioDTO} parcial con la nueva contraseña.
     *
     * @param id          Identificador del usuario cuya contraseña se actualizará.
     * @param newPassword Nueva contraseña a asignar (sin cifrar).
     * @return {@link Call} que, al ejecutarse, devuelve el {@link UsuarioDTO} con la contraseña actualizada.
     */
    public Call<UsuarioDTO> updatePassword(int id, String newPassword) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(id);
        dto.setPassword(newPassword);
        return apiService.updateUser(id, dto);
    }

    /**
     * Valida si la contraseña actual proporcionada coincide con la registrada para un usuario.
     *
     * @param username Nombre de usuario que se desea validar.
     * @param password Contraseña en texto plano a verificar.
     * @return {@link Call} que, al ejecutarse, devuelve un Void si la contraseña es correcta
     *         o un error en caso contrario.
     */
    public Call<Void> validateCurrentPassword(String username, String password) {
        return apiService.validateCurrentPassword(username, new PasswordDTO(password));
    }

    /**
     * Cambia la contraseña de un usuario identificado por su ID.
     *
     * @param userId      Identificador del usuario cuya contraseña se cambiará.
     * @param newPassword Nueva contraseña en texto plano a asignar.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link UsuarioDTO} actualizado con la nueva contraseña.
     */
    public Call<UsuarioDTO> changePassword(int userId, String newPassword) {
        return apiService.changePassword(userId, new PasswordDTO(newPassword));
    }

    /**
     * Actualiza la contraseña de un usuario utilizando un DTO específico para la contraseña.
     *
     * @param id          Identificador del usuario cuya contraseña se actualizará.
     * @param newPassword Nueva contraseña en texto plano a asignar.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link UsuarioDTO} con la contraseña actualizada.
     */
    public Call<UsuarioDTO> updatePasswordById(int id, String newPassword) {
        PasswordDTO dto = new PasswordDTO();
        dto.setPassword(newPassword);
        return apiService.updatePassword(id, dto);
    }
}
