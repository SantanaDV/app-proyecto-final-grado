package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO (Data Transfer Object) utilizado para eliminar un ejercicio.
 * Contiene únicamente el nombre del ejercicio que se desea eliminar.
 * 
 * Esta clase se utiliza para transferir los datos necesarios en la operación
 * de borrado de ejercicios entre el cliente y el servidor.
 * 
 * @author TuNombre
 */
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
