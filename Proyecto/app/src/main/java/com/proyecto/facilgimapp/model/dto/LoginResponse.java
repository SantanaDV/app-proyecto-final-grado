package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Clase que representa la respuesta de inicio de sesión recibida desde el backend.
 * Contiene información relevante como el token de autenticación, nombre de usuario,
 * mensaje de respuesta, roles/autorizaciones y el identificador del usuario.
 *
 * @author Francisco Santana
 */
public class LoginResponse implements Serializable {
    @SerializedName("token")
    private String token;

    @SerializedName("username")
    private String username;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("authorities")
    private List<String> authorities;

    @SerializedName("userId")
    private Integer userId;

    public LoginResponse() { }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public boolean isAdmin() {
        return authorities != null && authorities.contains("ROLE_ADMIN");
    }
}
