package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class UsuarioDTO {
    @SerializedName("idUsuario")
    private Integer idUsuario;

    private String username;
    private String password;
    private String correo;
    private String nombre;
    private String apellido;
    /**
     * Representa la dirección del usuario.
     * 
     * @author Francisco Santana
     */
    private String direccion;

    @SerializedName("admin")
    private boolean admin;

    public UsuarioDTO() {
    }

    public UsuarioDTO(boolean admin, String apellido, String direccion, String correo, String nombre, Integer idUsuario, String password, String username) {
        this.admin = admin;
        this.apellido = apellido;
        this.direccion = direccion;
        this.correo = correo;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.password = password;
        this.username = username;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    //Sobreescrito para mostrar solo el nombre
    @Override
    public String toString() {
        return getNombreCompleto();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioDTO that = (UsuarioDTO) o;
        return admin == that.admin && Objects.equals(idUsuario, that.idUsuario) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(correo, that.correo) && Objects.equals(nombre, that.nombre) && Objects.equals(apellido, that.apellido) && Objects.equals(direccion, that.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, username, password, correo, nombre, apellido, direccion, admin);
    }
}
