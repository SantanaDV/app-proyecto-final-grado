package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EntrenamientoDTO implements Serializable {
    @SerializedName("idEntrenamiento")
    private Integer id;

    private String nombre;
    private LocalDate fechaEntrenamiento;
    private String descripcion;
    private int duracion;

    @SerializedName("tipoEntrenamientoId")
    private Integer tipoEntrenamientoId;

    @SerializedName("usuarioId")
    private Integer usuarioId;

    @SerializedName("ejerciciosId")
    private List<Integer> ejerciciosId;

    @SerializedName("entrenamientosEjercicios")
    private List<EntrenamientoEjercicioDTO> entrenamientosEjercicios;

    // === Getters / Setters ===
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaEntrenamiento() { return fechaEntrenamiento; }
    public void setFechaEntrenamiento(LocalDate fechaEntrenamiento) { this.fechaEntrenamiento = fechaEntrenamiento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public Integer getTipoEntrenamientoId() { return tipoEntrenamientoId; }
    public void setTipoEntrenamientoId(Integer tipoEntrenamientoId) { this.tipoEntrenamientoId = tipoEntrenamientoId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public List<Integer> getEjerciciosId() { return ejerciciosId; }
    public void setEjerciciosId(List<Integer> ejerciciosId) { this.ejerciciosId = ejerciciosId; }

    public List<EntrenamientoEjercicioDTO> getEntrenamientosEjercicios() {
        return entrenamientosEjercicios;
    }

    public void setEntrenamientosEjercicios(List<EntrenamientoEjercicioDTO> entrenamientosEjercicios) {
        this.entrenamientosEjercicios = entrenamientosEjercicios;
    }

    public boolean isValid() {
        return nombre != null && !nombre.isEmpty()
                && fechaEntrenamiento != null
                && (ejerciciosId != null && !ejerciciosId.isEmpty()
                || (entrenamientosEjercicios != null && !entrenamientosEjercicios.isEmpty()));
    }

    public String convertirLFechaAString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.fechaEntrenamiento.format(formatter);
    }
}
