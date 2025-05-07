package com.proyecto.facilgimapp.repository.workout;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Repositorio para obtener la lista de EjercicioDTO
 * asociada a un entrenamiento + usuario.
 */
public class TrainingExerciseRepository {
    private final ApiService api;

    public TrainingExerciseRepository(Context ctx) {
        api = RetrofitClient.getApiService(ctx);
    }

    /**
     * Dispara la llamada al endpoint que devuelve todos los EjercicioDTO
     * para un entrenamiento y usuario dados.
     */
    public void fetchExercisesForWorkout(int workoutId,
                                         String username,
                                         Callback<List<EjercicioDTO>> cb) {
        api.listExercisesByTraining(workoutId, username)
                .enqueue(cb);
    }

    /** Para tests o chaining */
    public Call<List<EjercicioDTO>> listExercisesForWorkout(int workoutId,
                                                            String username) {
        return api.listExercisesByTraining(workoutId, username);
    }
}
