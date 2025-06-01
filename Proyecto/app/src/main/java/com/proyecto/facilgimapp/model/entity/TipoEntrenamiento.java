
package com.proyecto.facilgimapp.model.entity;

import com.google.gson.annotations.SerializedName;
/**
 * Representa un tipo de entrenamiento en la aplicación.
 * Contiene el identificador y el nombre del tipo de entrenamiento.
 *
 * @author Francisco Santana
 */
public class TipoEntrenamiento {
    @SerializedName("id")
    private Integer id;
    @SerializedName("nombre")
    private String nombre;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
}
