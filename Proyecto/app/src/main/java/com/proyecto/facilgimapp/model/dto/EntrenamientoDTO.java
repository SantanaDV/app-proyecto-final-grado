package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class EntrenamientoDTO implements Serializable {
    private String nombre;
    private LocalDate fechaEntrenamiento;
    private String descripcion;
    private int duracion;

    @SerializedName("tipoEntrenamientoId")
    private Long tipoEntrenamientoId;

    @SerializedName("usuarioId")
    private Integer usuarioId;

    @SerializedName("ejerciciosId")
    private List<Integer> ejerciciosId;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }
    public void setFechaEntrenamiento(LocalDate fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracion() {
        return duracion;
    }
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public Long getTipoEntrenamientoId() {
        return tipoEntrenamientoId;
    }
    public void setTipoEntrenamientoId(Long tipoEntrenamientoId) {
        this.tipoEntrenamientoId = tipoEntrenamientoId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<Integer> getEjerciciosId() {
        return ejerciciosId;
    }
    public void setEjerciciosId(List<Integer> ejerciciosId) {
        this.ejerciciosId = ejerciciosId;
    }
}
