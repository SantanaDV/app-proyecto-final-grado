package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.repository.WorkoutRepository;

import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutViewModel extends AndroidViewModel {
    private final WorkoutRepository repo;
    private final MutableLiveData<List<EntrenamientoDTO>> _workouts = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<EntrenamientoDTO>> getWorkouts() {
        return _workouts;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        repo = new WorkoutRepository(application.getApplicationContext());
    }

    /** Método modificado para recibir el usuarioId y cargar solo los entrenamientos del usuario */
    public void loadWorkoutsByUserId(int userId) {
        repo.getWorkoutsByUserId(userId).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoDTO>> call, Response<List<EntrenamientoDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _workouts.setValue(response.body());
                    Log.d("DEBUG", "Entrenamientos cargados: " + response.body());  // Aquí

                } else {
                    _workouts.setValue(Collections.emptyList());
                    errorMessage.setValue("No se encontraron entrenamientos para este usuario.");
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoDTO>> call, Throwable t) {
                _workouts.setValue(Collections.emptyList());
                errorMessage.setValue("Error al cargar los entrenamientos: " + t.getMessage());
            }
        });
    }

}
