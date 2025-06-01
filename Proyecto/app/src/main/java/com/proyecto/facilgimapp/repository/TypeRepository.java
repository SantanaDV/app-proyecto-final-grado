package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
/**
 * Repositorio encargado de gestionar las operaciones CRUD de tipos de entrenamiento
 * contra la API REST mediante Retrofit. Incluye métodos para listar, crear, actualizar
 * y eliminar tipos de entrenamiento.
 *
 * @autor: Francisco Santana
 */
public class TypeRepository {
    private final ApiService apiService;
    /**
     * Constructor que inicializa el servicio de API obteniéndolo de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public TypeRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }
    /**
     * Lista todos los tipos de entrenamiento disponibles.
     *
     * @return Un objeto Call que representa la solicitud para obtener la lista de tipos de entrenamiento.
     */
    public Call<List<TipoEntrenamientoDTO>> listTypes() {
        return apiService.listTypes();
    }
    /**
     * Crea un nuevo tipo de entrenamiento o actualiza uno existente.
     *
     * @param dto Objeto TipoEntrenamientoDTO que contiene los datos del tipo de entrenamiento a crear o actualizar.
     * @return Un objeto Call que representa la solicitud para crear o actualizar el tipo de entrenamiento.
     */
    public Call<TipoEntrenamientoDTO> createType(TipoEntrenamientoDTO dto) {
        return apiService.createType(dto);
    }

    /**
     * Actualiza un tipo de entrenamiento existente por su ID.
     *
     * @param id El identificador del tipo de entrenamiento a actualizar.
     * @param dto Objeto TipoEntrenamientoDTO que contiene los datos actualizados del tipo de entrenamiento.
     * @return Un objeto Call que representa la solicitud para actualizar el tipo de entrenamiento.
     */
    public Call<TipoEntrenamientoDTO> updateType(int id, TipoEntrenamientoDTO dto) {
        return apiService.updateType(id, dto);
    }
    /**
     * Elimina un tipo de entrenamiento por su ID.
     *
     * @param id El identificador del tipo de entrenamiento a eliminar.
     * @return Un objeto Call que representa la solicitud para eliminar el tipo de entrenamiento.
     */
    public Call<Void> deleteType(int id) {
        return apiService.deleteType(id);
    }
    /**
     * Obtiene un tipo de entrenamiento específico por su ID.
     *
     * @param id El identificador del tipo de entrenamiento a obtener.
     * @return Un objeto Call que representa la solicitud para obtener el tipo de entrenamiento.
     */
    public Call<TipoEntrenamientoDTO> getType(int id) {
        return apiService.getType(id);
    }
}
