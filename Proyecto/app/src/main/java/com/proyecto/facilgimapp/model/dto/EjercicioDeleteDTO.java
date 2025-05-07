package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
public class EjercicioDeleteDTO {
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("usernamePropietario")
    private String usernamePropietario;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsernamePropietario() {
        return usernamePropietario;
    }

    public void setUsernamePropietario(String usernamePropietario) {
        this.usernamePropietario = usernamePropietario;
    }
}
