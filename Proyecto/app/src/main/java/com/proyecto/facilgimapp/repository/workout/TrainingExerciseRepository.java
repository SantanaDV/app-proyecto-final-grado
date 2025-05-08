// com.proyecto.facilgimapp.repository.workout.TrainingExerciseRepository
package com.proyecto.facilgimapp.repository.workout;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

/**
 * Repositorio para obtener las relaciones ejercicio–entrenamiento.
 */
public class TrainingExerciseRepository {
    private final ApiService api;

    public TrainingExerciseRepository(Context ctx) {
        api = RetrofitClient.getApiService(ctx);
    }

    /**
     * Devuelve la lista de ejercicios asignados a un entrenamiento.
     */
    public Call<List<EntrenamientoEjercicioDTO>> listExercisesForWorkout(int workoutId) {
        return api.listExercisesInTraining(workoutId);
    }
}
