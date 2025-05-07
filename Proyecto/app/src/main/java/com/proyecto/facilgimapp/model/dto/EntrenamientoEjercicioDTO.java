package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import com.proyecto.facilgimapp.model.dto.SerieDTO;

import java.util.List;

public class EntrenamientoEjercicioDTO {
    @SerializedName("idEntrenamiento")
    private Integer idEntrenamiento;

    @SerializedName("idEjercicio")
    private Integer idEjercicio;

    @SerializedName("series")
    private List<SerieDTO> series;

    public EntrenamientoEjercicioDTO() { }

    public EntrenamientoEjercicioDTO(Integer idEntrenamiento, Integer idEjercicio, List<SerieDTO> series) {
        this.idEntrenamiento = idEntrenamiento;
        this.idEjercicio = idEjercicio;
        this.series = series;
    }

    public EntrenamientoEjercicioDTO(int entrenamientoId, int ejercicioId, List<com.proyecto.facilgimapp.model.dto.SerieDTO> series) {
    }

    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(Integer idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public Integer getIdEjercicio() { return idEjercicio; }
    public void setIdEjercicio(Integer idEjercicio) { this.idEjercicio = idEjercicio; }

    public List<SerieDTO> getSeries() { return series; }
    public void setSeries(List<SerieDTO> series) { this.series = series; }
}
