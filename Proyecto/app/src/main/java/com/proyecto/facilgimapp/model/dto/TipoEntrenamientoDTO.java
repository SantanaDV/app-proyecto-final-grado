package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import com.proyecto.facilgimapp.model.entity.TipoEntrenamiento;

import java.util.Objects;

/**
 * DTO (Data Transfer Object) para representar un tipo de entrenamiento.
 * Esta clase se utiliza para transferir datos entre capas de la aplicación,
 * especialmente en operaciones relacionadas con la serialización y deserialización JSON.
 * 
 * Contiene los atributos identificador y nombre del tipo de entrenamiento.
 * 
 * @author Francisco Santana
 */
public class TipoEntrenamientoDTO {
    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;


    public TipoEntrenamientoDTO() {
    }

    public TipoEntrenamientoDTO(String nombre) {
        this.nombre = nombre;
    }

    public TipoEntrenamientoDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public TipoEntrenamientoDTO(TipoEntrenamiento tipoEntrenamiento) {
        this.id = tipoEntrenamiento.getId();
        this.nombre = tipoEntrenamiento.getNombre();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TipoEntrenamientoDTO that = (TipoEntrenamientoDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }
}
