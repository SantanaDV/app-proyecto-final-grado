package com.proyecto.facilgimapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Usuario {
    @SerializedName("idUsuario")
    private int idUsuario;
    @SerializedName("username")
    private String username;
    @SerializedName("correo")
    private String correo;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("apellido")
    private String apellido;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
