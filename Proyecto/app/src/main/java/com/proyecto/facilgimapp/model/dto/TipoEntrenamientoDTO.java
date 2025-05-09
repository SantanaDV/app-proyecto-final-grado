package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

public class TipoEntrenamientoDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    public TipoEntrenamientoDTO() { }

    public TipoEntrenamientoDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    //Sobreescrito para mostrar solo el nombre
    @Override
    public String toString() {
        return nombre;
    }

}
