package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import com.proyecto.facilgimapp.model.Entrenamiento;

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

    @SerializedName("tipoEntrenamiento")
    private TipoEntrenamientoDTO tipoEntrenamiento;


    @SerializedName("usuario")
    private UsuarioDTO usuario;

    @SerializedName("ejerciciosId")
    private List<Integer> ejerciciosId;

    @SerializedName("entrenamientosEjercicios")
    private List<EntrenamientoEjercicioDTO> entrenamientosEjercicios;

    public EntrenamientoDTO() {
    }

    public EntrenamientoDTO(Entrenamiento entrenamiento) {
        this.id = entrenamiento.getId();
        this.nombre = entrenamiento.getNombre();
        this.fechaEntrenamiento = LocalDate.parse(entrenamiento.getFechaEntrenamiento());
        this.descripcion = entrenamiento.getDescripcion();
        this.duracion = entrenamiento.getDuracionMinutos();
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
