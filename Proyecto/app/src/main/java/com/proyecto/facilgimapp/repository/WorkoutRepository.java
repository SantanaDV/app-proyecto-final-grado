package com.proyecto.facilgimapp.repository;

import android.content.Context;

import com.proyecto.facilgimapp.model.entity.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;

/**
 * Repositorio encargado de gestionar las operaciones CRUD de entrenamientos
 * contra la API REST mediante Retrofit. Incluye métodos para obtener, crear,
 * actualizar y eliminar entrenamientos, así como para consultar entrenamientos
 * por usuario o por rango de fechas.
 * 
 * @author Francisco Santana
 */
public class WorkoutRepository {
    /**
     * Servicio de API proporcionado por Retrofit para realizar las llamadas HTTP.
     */
    private final ApiService apiService;

    /**
     * Constructor que inicializa el servicio de API obteniéndolo de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public WorkoutRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    /**
     * Obtiene la lista completa de entrenamientos.
     *
     * @return {@link Call} que, al ejecutarse, devuelve una lista de {@link Entrenamiento}.
     */
    public Call<List<Entrenamiento>> getWorkouts() {
        return apiService.listAllTrainings();
    }

    /**
     * Obtiene un entrenamiento concreto a partir de su identificador.
     *
     * @param id Identificador único del entrenamiento.
     * @return {@link Call} que, al ejecutarse, devuelve un {@link Entrenamiento}.
     */
    public Call<Entrenamiento> getWorkout(int id) {
        return apiService.getTraining(id);
    }

    /**
     * Obtiene los entrenamientos cuya fecha se encuentra entre dos valores (inclusive).
     *
     * @param fromDate Fecha de inicio en formato ISO (por ejemplo, "2025-06-01").
     * @param toDate   Fecha de fin en formato ISO (por ejemplo, "2025-06-30").
     * @return {@link Call} que, al ejecutarse, devuelve una lista de {@link Entrenamiento}.
     */
    public Call<List<Entrenamiento>> getWorkoutsBetween(String fromDate, String toDate) {
        return apiService.listTrainingsBetweenDates(fromDate, toDate);
    }

    /**
     * Crea un nuevo entrenamiento en el servidor.
     *
     * @param dto Objeto {@link EntrenamientoDTO} con los datos necesarios para crear el entrenamiento.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link EntrenamientoDTO} creado.
     */
    public Call<EntrenamientoDTO> createWorkout(EntrenamientoDTO dto) {
        return apiService.createTraining(dto);
    }

    /**
     * Actualiza un entrenamiento existente a partir de su identificador.
     *
     * @param id  Identificador del entrenamiento a actualizar.
     * @param dto {@link EntrenamientoDTO} que contiene los datos actualizados del entrenamiento.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link Entrenamiento} actualizado.
     */
    public Call<Entrenamiento> updateWorkout(int id, EntrenamientoDTO dto) {
        return apiService.updateTraining(id, dto);
    }

    /**
     * Elimina un entrenamiento a partir de su identificador.
     *
     * @param id Identificador del entrenamiento a eliminar.
     * @return {@link Call} que, al ejecutarse, no devuelve contenido (Void).
     */
    public Call<Void> deleteWorkout(int id) {
        return apiService.deleteTraining(id);
    }

    /**
     * Obtiene la lista de entrenamientos asociados a un usuario específico.
     *
     * @param id Identificador del usuario.
     * @return {@link Call} que, al ejecutarse, devuelve una lista de {@link EntrenamientoDTO}.
     */
    public Call<List<EntrenamientoDTO>> getWorkoutsByUserId(int id) {
        return apiService.getWorkoutsByUserId(id);
    }

    /**
     * Actualiza un entrenamiento utilizando un DTO específico.
     * <p>
     * Este método sirve para casos en que la actualización se envía con un objeto
     * {@link EntrenamientoDTO} y el endpoint espera dicho DTO.
     *
     * @param id  Identificador del entrenamiento a actualizar.
     * @param dto {@link EntrenamientoDTO} con los cambios a aplicar.
     * @return {@link Call} que, al ejecutarse, devuelve el {@link Entrenamiento} actualizado.
     */
    public Call<Entrenamiento> updateWorkoutFromDto(int id, EntrenamientoDTO dto) {
        return apiService.updateWorkoutFromDto(id, dto);
    }
}
