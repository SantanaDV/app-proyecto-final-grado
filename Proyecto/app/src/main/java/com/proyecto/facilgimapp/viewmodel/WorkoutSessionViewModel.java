package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutSessionViewModel extends AndroidViewModel {
    private final WorkoutRepository workoutRepo;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public WorkoutSessionViewModel(@NonNull Application application) {
        super(application);
        workoutRepo = new WorkoutRepository(application.getApplicationContext());
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void saveWorkoutSession(EntrenamientoDTO workoutDTO, Map<EjercicioDTO, List<SerieDTO>> seriesMap) {
        // Construye la relación ejercicio-series
        List<EntrenamientoEjercicioDTO> relaciones = new ArrayList<>();

        int orden = 1;
        for (Map.Entry<EjercicioDTO, List<SerieDTO>> entry : seriesMap.entrySet()) {
            EntrenamientoEjercicioDTO relacion = new EntrenamientoEjercicioDTO();
            relacion.setEjercicio(entry.getKey());
            relacion.setSeries(entry.getValue());
            relacion.setOrden(orden++); // Establece el orden de aparición
            relaciones.add(relacion);
        }

        // Asignamos al DTO principal
        workoutDTO.setEntrenamientosEjercicios(relaciones);

        // Validación adicional
        if (workoutDTO.getUsuario() == null || workoutDTO.getUsuario().getIdUsuario() == null
                || workoutDTO.getTipoEntrenamiento() == null)
        {
            errorMessage.setValue("Faltan datos obligatorios para guardar el entrenamiento.");
            return;
        }

        // Envío a API
        workoutRepo.createWorkout(workoutDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<EntrenamientoDTO> call, Response<EntrenamientoDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    errorMessage.setValue("Error al guardar el entrenamiento.");
                }
            }

            @Override
            public void onFailure(Call<EntrenamientoDTO> call, Throwable t) {
                errorMessage.setValue("Error al guardar el entrenamiento: " + t.getMessage());
            }
        });
    }

}
