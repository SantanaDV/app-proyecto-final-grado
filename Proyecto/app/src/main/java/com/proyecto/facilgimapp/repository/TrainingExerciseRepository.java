package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
/**
 * Repositorio encargado de gestionar las operaciones CRUD de ejercicios
 * asociados a entrenamientos contra la API REST mediante Retrofit.
 * Incluye métodos para listar, obtener, añadir y eliminar relaciones
 * entre ejercicios y entrenamientos.
 * 
 * @autor: Francisco Santana
 */

public class TrainingExerciseRepository {
    private final ApiService api;
    /**
     * Constructor que inicializa el servicio de API obteniéndolo de RetrofitClient.
     *
     * @param ctx Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public TrainingExerciseRepository(Context ctx) {
        api = RetrofitClient.getApiService(ctx);
    }

    /**
     * Lista todos los ejercicios asociados a un entrenamiento específico.
     *
     * @param workoutId El identificador del entrenamiento para el cual se desean listar los ejercicios.
     * @return Un objeto Call que representa la solicitud para obtener la lista de ejercicios.
     */
    public Call<List<EntrenamientoEjercicioDTO>> listExercisesForWorkout(int workoutId) {
        return api.listExercisesInTraining(workoutId);
    }
    /**
     * Obtiene una relación específica entre un ejercicio y un entrenamiento por su ID.
     *
     * @param id El identificador de la relación entre el ejercicio y el entrenamiento.
     * @return Un objeto Call que representa la solicitud para obtener la relación.
     */
    public Call<EntrenamientoEjercicioDTO> getRelationById(int id) {
        return api.getTrainingExerciseRelation(id);
    }
    /**
     * Añade un nuevo ejercicio a un entrenamiento.
     *
     * @param dto Objeto EntrenamientoEjercicioDTO que contiene los datos del ejercicio a añadir.
     * @return Un objeto Call que representa la solicitud para añadir el ejercicio al entrenamiento.
     */
    public Call<EntrenamientoEjercicioDTO> addExerciseToTraining(EntrenamientoEjercicioDTO dto) {
        return api.addExerciseToTraining(dto);
    }
    /**
     * Actualiza una relación existente entre un ejercicio y un entrenamiento.
     *
     * @param id El identificador de la relación a actualizar.
     * @param dto Objeto EntrenamientoEjercicioDTO que contiene los datos actualizados del ejercicio.
     * @return Un objeto Call que representa la solicitud para actualizar la relación.
     */
    public Call<Void> deleteRelation(int id) {
        return api.removeExerciseFromTraining(id);
    }
}
