package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.entity.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.repository.TrainingExerciseRepository;
import com.proyecto.facilgimapp.repository.WorkoutRepository;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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

    /**
     * Método modificado para recibir el usuarioId y cargar solo los entrenamientos del usuario
     */
    public void loadWorkoutsByUserId(int userId) {
        repo.getWorkoutsByUserId(userId).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoDTO>> call, Response<List<EntrenamientoDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    _workouts.setValue(response.body());
                    Log.d("DEBUG", "Entrenamientos cargados: " + response.body());

                } else {
                    _workouts.setValue(Collections.emptyList());
                    errorMessage.setValue(getApplication().getString(R.string.error_cargar_entrenamientos));
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoDTO>> call, Throwable t) {
                _workouts.setValue(Collections.emptyList());
                errorMessage.setValue(R.string.error_cargar_entrenamientos + ": " + t.getMessage());
            }
        });
    }

    public void deleteWorkout(int workoutId, Runnable onSuccess, Runnable onFailure) {
        repo.deleteWorkout(workoutId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_eliminar_entrenamiento));
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.setValue(R.string.error_eliminar_entrenamiento + ": " + t.getMessage());
                onFailure.run();
            }
        });
    }

    public void updateWorkout(int id, EntrenamientoDTO dto, Runnable onSuccess, Runnable onFailure) {
        repo.updateWorkoutFromDto(id, dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Entrenamiento> call, Response<Entrenamiento> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_actualizar_entrenamiento));
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Entrenamiento> call, Throwable t) {
                errorMessage.setValue( R.string.error_actualizar_entrenamiento + ": " + t.getMessage());
                onFailure.run();
            }
        });
    }

    public void loadWorkoutRelations(int workoutId, Consumer<List<EntrenamientoEjercicioDTO>> onResult) {
        new TrainingExerciseRepository(getApplication().getApplicationContext())
                .listExercisesForWorkout(workoutId)
                .enqueue(new Callback<List<EntrenamientoEjercicioDTO>>() {
                    @Override
                    public void onResponse(Call<List<EntrenamientoEjercicioDTO>> call, Response<List<EntrenamientoEjercicioDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            onResult.accept(response.body());
                        } else {
                            Log.e("DEBUG", "No se pudieron obtener las relaciones");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EntrenamientoEjercicioDTO>> call, Throwable t) {
                        Log.e("DEBUG", "Error al cargar relaciones: " + t.getMessage());
                    }
                });
    }

}
