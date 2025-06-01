package com.proyecto.facilgimapp.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo que mapea la entidad Entrenamiento del backend.
 */
public class Entrenamiento {
    @SerializedName("idEntrenamiento")
    private Integer idEntrenamiento;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fechaEntrenamiento")
    private String fechaEntrenamiento;

    @SerializedName("duracion")
    private int duracion;
    @SerializedName("tipoEntrenamiento")
    private TipoEntrenamiento tipoEntrenamiento;

    public TipoEntrenamiento getTipoEntrenamiento() {
        return tipoEntrenamiento;
    }
    public void setTipoEntrenamiento(TipoEntrenamiento tipoEntrenamiento) {
        this.tipoEntrenamiento = tipoEntrenamiento;
    }

    private List<Ejercicio> ejercicios = new ArrayList<>();

    public Integer getIdEntrenamiento() {
        return idEntrenamiento;
    }

    public Integer getId() {
        return idEntrenamiento;
    }

    public void setIdEntrenamiento(Integer idEntrenamiento) {
        this.idEntrenamiento = idEntrenamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(String fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public int getDuracionMinutos() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }


    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public static String formatDuracion(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        return (horas > 0 ? horas + "h " : "") + mins + "min";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrenamiento that = (Entrenamiento) o;
        return Objects.equals(idEntrenamiento, that.idEntrenamiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEntrenamiento);
    }

}