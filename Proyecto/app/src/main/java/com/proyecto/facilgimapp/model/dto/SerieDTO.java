package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class SerieDTO {
    @SerializedName("id")
    private Integer id;

    @SerializedName("numeroSerie")
    private Integer numeroSerie;

    /**
     * Clase SerieDTO que representa una serie de ejercicios con su número de repeticiones.
     * 
     * @author Francisco Santana
     */
    @SerializedName("repeticiones")
    private Integer repeticiones;

    @SerializedName("peso")
    private Double peso;
    private boolean completada;

    public SerieDTO() { }

    public SerieDTO(Integer id, Integer numeroSerie, Integer repeticiones, Double peso) {
        this.id = id;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public SerieDTO( Integer numeroSerie, Integer repeticiones, Double peso) {
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(Integer numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Integer getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(Integer repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
    public boolean isCompletada() {
        return completada;
    }
    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SerieDTO serieDTO = (SerieDTO) o;
        return completada == serieDTO.completada && Objects.equals(id, serieDTO.id) && Objects.equals(numeroSerie, serieDTO.numeroSerie) && Objects.equals(repeticiones, serieDTO.repeticiones) && Objects.equals(peso, serieDTO.peso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroSerie, repeticiones, peso, completada);
    }
}
