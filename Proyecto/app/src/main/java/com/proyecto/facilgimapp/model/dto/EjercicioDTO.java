package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import com.proyecto.facilgimapp.model.Ejercicio;

import java.util.Objects;

public class EjercicioDTO {
    @SerializedName("idEjercicio")
    private Integer idEjercicio;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("imagenUrl")
    private String imagenUrl;

    // Si en el futuro añades más campos (p.ej. repeticiones), inclúyelos también aquí.

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EjercicioDTO)) return false;
        EjercicioDTO that = (EjercicioDTO) o;
        return Objects.equals(idEjercicio, that.idEjercicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEjercicio);
    }
    public Ejercicio toEjercicio() {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setIdEjercicio(idEjercicio);
        ejercicio.setNombre(nombre);
        ejercicio.setImagenUrl(imagenUrl);
        return ejercicio;
    }
}
