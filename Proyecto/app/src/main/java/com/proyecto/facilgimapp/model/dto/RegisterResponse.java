package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Clase que representa la respuesta recibida tras el registro de un usuario.
 * Contiene los datos principales del usuario registrado.
 * 
 * @author Francisco Santana
 */
public class RegisterResponse {
    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("username")
    private String username;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    public RegisterResponse() { }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
