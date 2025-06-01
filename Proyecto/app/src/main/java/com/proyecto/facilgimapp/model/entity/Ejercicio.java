
package com.proyecto.facilgimapp.model.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;

/**
 * Representa un ejercicio dentro de la aplicación FacilGimApp.
 * Contiene información relevante como el identificador, nombre, URL de la imagen asociada
 * y la lista de series que componen el ejercicio.
 *
 * @author Francisco Santana
 */
public class Ejercicio {
    @SerializedName("idEjercicio")
    private Integer idEjercicio;

    private String nombre;
    @Nullable
    private String imagenUrl;

    private List<Serie> series;

    public Integer getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(Integer idEjercicio) {
        this.idEjercicio = idEjercicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ejercicio that = (Ejercicio) o;
        return Objects.equals(idEjercicio, that.idEjercicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEjercicio);
    }
}
