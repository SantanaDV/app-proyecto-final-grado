package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
/**
 * Repositorio encargado de gestionar las operaciones CRUD de series
 * contra la API REST mediante Retrofit. Incluye métodos para listar,
 * obtener, crear/actualizar y eliminar series.
 * 
 * @autor: Francisco Santana
 */
public class SeriesRepository {
    private final ApiService apiService;
    /**
     * Inicializa el repositorio obteniendo el ApiService de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public SeriesRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }
    /**
     * Lista todas las series asociadas a una relación específica.
     *
     * @param relacionId El identificador de la relación para la cual se desean listar las series.
     * @return Un objeto Call que representa la solicitud para obtener la lista de series.
     */
    public Call<List<SerieDTO>> listSeries(int relacionId) {
        return apiService.listSeries(relacionId);
    }
    /**
     * Obtiene una serie específica por su ID.
     *
     * @param id El identificador de la serie a obtener.
     * @return Un objeto Call que representa la solicitud para obtener la serie.
     */
    public Call<SerieDTO> getSeries(int id) {
        return apiService.getSeries(id);
    }
    /**
     * Crea una nueva serie o actualiza una existente.
     *
     * @param dto Objeto SerieDTO que contiene los datos de la serie a crear o actualizar.
     * @return Un objeto Call que representa la solicitud para crear o actualizar la serie.
     */
    public Call<SerieDTO> createSeries(SerieDTO dto) {
        return apiService.createSeries(dto);
    }
    /**
     * Actualiza una serie existente por su ID.
     *
     * @param id El identificador de la serie a actualizar.
     * @param dto Objeto SerieDTO que contiene los datos actualizados de la serie.
     * @return Un objeto Call que representa la solicitud para actualizar la serie.
     */    
    public Call<SerieDTO> updateSeries(int id, SerieDTO dto) {
        return apiService.updateSeries(id, dto);
    }
    /**
     * Elimina una serie específica por su ID.
     *
     * @param id El identificador de la serie a eliminar.
     * @return Un objeto Call que representa la solicitud para eliminar la serie.
     */
    public Call<Void> deleteSeries(int id) {
        return apiService.deleteSeries(id);
    }
    /**
     * Crea múltiples series a partir de una lista de objetos SerieDTO.
     *
     * @param series Lista de objetos SerieDTO que representan las series a crear.
     * @return Una lista de objetos Call que representan las solicitudes para crear cada serie.
     */
    public List<Call<SerieDTO>> createMultipleSeries(List<SerieDTO> series) {
        List<Call<SerieDTO>> calls = new ArrayList<>();
        for (SerieDTO serie : series) {
            calls.add(apiService.createSeries(serie));
        }
        return calls;
    }
}
