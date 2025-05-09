package com.proyecto.facilgimapp.repository;

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

    // Obtener todos los entrenamientos
    public Call<List<Entrenamiento>> getWorkouts() {
        return apiService.listAllTrainings();
    }

    // Obtener un entrenamiento por su ID
    public Call<Entrenamiento> getWorkout(int id) {
        return apiService.getTraining(id);
    }

    // Obtener entrenamientos entre dos fechas
    public Call<List<Entrenamiento>> getWorkoutsBetween(String fromDate, String toDate) {
        return apiService.listTrainingsBetweenDates(fromDate, toDate);
    }

    // Crear un nuevo entrenamiento
    public Call<EntrenamientoDTO> createWorkout(EntrenamientoDTO dto) {
        return apiService.createTraining(dto);
    }

    // Actualizar un entrenamiento existente
    public Call<Entrenamiento> updateWorkout(int id, EntrenamientoDTO dto) {
        return apiService.updateTraining(id, dto);
    }

    // Eliminar un entrenamiento
    public Call<Void> deleteWorkout(int id) {
        return apiService.deleteTraining(id);
    }


    public Call<List<EntrenamientoDTO>> getWorkoutsByUserId(int id) {
        return apiService.getWorkoutsByUserId(id);
    }


    // Actualizar un entrenamiento con DTO
    public Call<Entrenamiento> updateWorkoutFromDto(int id, EntrenamientoDTO dto) {
        return apiService.updateWorkoutFromDto(id, dto);
    }
}
