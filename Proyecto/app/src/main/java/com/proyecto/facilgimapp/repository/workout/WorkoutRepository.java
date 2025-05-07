package com.proyecto.facilgimapp.repository.workout;

import android.content.Context;
import com.proyecto.facilgimapp.model.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class WorkoutRepository {
    private final ApiService apiService;

    public WorkoutRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<List<Entrenamiento>> getWorkouts() {
        return apiService.listAllTrainings();
    }

    public Call<Entrenamiento> getWorkout(int id) {
        return apiService.getTraining(id);
    }

    public Call<List<Entrenamiento>> getWorkoutsBetween(String fromDate, String toDate) {
        return apiService.listTrainingsBetweenDates(fromDate, toDate);
    }

    public Call<Entrenamiento> createWorkout(EntrenamientoDTO dto) {
        return apiService.createTraining(dto);
    }

    public Call<Entrenamiento> updateWorkout(int id, EntrenamientoDTO dto) {
        return apiService.updateTraining(id, dto);
    }

    public Call<Void> deleteWorkout(int id) {
        return apiService.deleteTraining(id);
    }
}
