package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

public class TipoEntrenamientoDTO {
    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;

    public TipoEntrenamientoDTO(String nombre) {
        this.nombre = nombre;
    }

    public TipoEntrenamientoDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public TipoEntrenamientoDTO(Integer id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    //Sobreescrito para mostrar solo el nombre
    @Override
    public String toString() {
        return nombre;
    }

}
