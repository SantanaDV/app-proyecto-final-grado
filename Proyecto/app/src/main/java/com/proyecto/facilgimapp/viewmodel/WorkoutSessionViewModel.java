package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.repository.WorkoutRepository;
import java.util.List;

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

    public void saveWorkoutSession(EntrenamientoDTO workoutDTO, List<SerieDTO> seriesList) {
        // Guardamos las series asociadas al entrenamiento
        workoutDTO.setSeries(seriesList);

        // Llamada al repositorio para guardar los datos en la API
        workoutRepo.createWorkout(workoutDTO).enqueue(new Callback<EntrenamientoDTO>() {
            @Override
            public void onResponse(Call<EntrenamientoDTO> call, Response<EntrenamientoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // El entrenamiento se ha guardado correctamente
                } else {
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
