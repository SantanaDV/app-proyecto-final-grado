package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;

public class EntrenamientoEjercicioDTO {

    @SerializedName("id")
    private Integer id;


    @SerializedName("ejercicio")
    private EjercicioDTO ejercicio;

    @SerializedName("series")
    private List<SerieDTO> series;

    private int orden;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public EjercicioDTO getEjercicio() { return ejercicio; }
    public void setEjercicio(EjercicioDTO ejercicio) { this.ejercicio = ejercicio; }

    public List<SerieDTO> getSeries() { return series; }
    public void setSeries(List<SerieDTO> series) { this.series = series; }
    public boolean isCompletado() {
        return series != null && series.stream().allMatch(SerieDTO::isCompletada);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntrenamientoEjercicioDTO)) return false;
        EntrenamientoEjercicioDTO that = (EntrenamientoEjercicioDTO) o;
        return Objects.equals(ejercicio, that.ejercicio)
                && Objects.equals(series, that.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ejercicio, series);
    }


}
