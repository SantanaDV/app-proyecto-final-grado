package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import com.proyecto.facilgimapp.model.entity.Entrenamiento;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class EntrenamientoDTO implements Serializable {
    /**
     * Data Transfer Object (DTO) que representa un entrenamiento.
     * 
     * Contiene el identificador único del entrenamiento.
     * 
     * @author Francisco Santana
     */
    @SerializedName("idEntrenamiento")
    private Integer id;

    private String nombre;

    private LocalDate fechaEntrenamiento;
    private String descripcion;
    private int duracion;

    @SerializedName("tipoEntrenamiento")
    private TipoEntrenamientoDTO tipoEntrenamiento;


    @SerializedName("usuario")
    private UsuarioDTO usuario;

    @SerializedName("ejerciciosId")
    private List<Integer> ejerciciosId;

    @SerializedName("entrenamientosEjercicios")
    private List<EntrenamientoEjercicioDTO> entrenamientosEjercicios;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EntrenamientoDTO that = (EntrenamientoDTO) o;
        return duracion == that.duracion && Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre) && Objects.equals(fechaEntrenamiento, that.fechaEntrenamiento) && Objects.equals(descripcion, that.descripcion) && Objects.equals(tipoEntrenamiento, that.tipoEntrenamiento) && Objects.equals(usuario, that.usuario) && Objects.equals(ejerciciosId, that.ejerciciosId) && Objects.equals(entrenamientosEjercicios, that.entrenamientosEjercicios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fechaEntrenamiento, descripcion, duracion, tipoEntrenamiento, usuario, ejerciciosId, entrenamientosEjercicios);
    }

    public EntrenamientoDTO() {
    }

    public EntrenamientoDTO(Entrenamiento entrenamiento) {
        this.id = entrenamiento.getId();
        this.nombre = entrenamiento.getNombre();
        this.fechaEntrenamiento = LocalDate.parse(entrenamiento.getFechaEntrenamiento());
        this.descripcion = entrenamiento.getDescripcion();
        this.duracion = entrenamiento.getDuracionMinutos();
        this.tipoEntrenamiento = new TipoEntrenamientoDTO(entrenamiento.getTipoEntrenamiento());
    }

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

    public TipoEntrenamientoDTO getTipoEntrenamiento() {
        return tipoEntrenamiento;
    }

    public void setTipoEntrenamiento(TipoEntrenamientoDTO tipoEntrenamiento) {
        this.tipoEntrenamiento = tipoEntrenamiento;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

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
