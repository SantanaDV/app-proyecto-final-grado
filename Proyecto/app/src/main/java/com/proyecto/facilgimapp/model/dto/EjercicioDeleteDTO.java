package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

public class EjercicioDeleteDTO {
    @SerializedName("nombre")
    private String nombre;

    public EjercicioDeleteDTO() {}

    public EjercicioDeleteDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
