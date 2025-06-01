package com.proyecto.facilgimapp.model.entity;

import com.google.gson.annotations.SerializedName;

public class TipoEntrenamiento {
    @SerializedName("id")
    private Integer id;
    @SerializedName("nombre")
    private String nombre;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
}
