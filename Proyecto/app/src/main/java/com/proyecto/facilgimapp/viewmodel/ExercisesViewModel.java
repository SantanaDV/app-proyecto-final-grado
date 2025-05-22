package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.repository.EjercicioRepository;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExercisesViewModel extends AndroidViewModel {
    private final EjercicioRepository repo;

    // Para ejercicios filtrados por entrenamiento
    private final MutableLiveData<List<EjercicioDTO>> exercises = new MutableLiveData<>();

    // Para catálogo completo
    private final MutableLiveData<List<EjercicioDTO>> allExercises = new MutableLiveData<>();

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        repo = new EjercicioRepository(application);
    }

    /** Ejercicios de un entrenamiento existente */
    public LiveData<List<EjercicioDTO>> getExercises() {
        return exercises;
    }

    /** Catálogo completo de ejercicios */
    public LiveData<List<EjercicioDTO>> getAllExercises() {
        return allExercises;
    }

    public void loadExercises(int trainingId, String username) {
        repo.listExercisesByTraining(trainingId, username)
                .enqueue(new Callback<List<EjercicioDTO>>() {
                    @Override
                    public void onResponse(Call<List<EjercicioDTO>> call,
                                           Response<List<EjercicioDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            exercises.setValue(response.body());
                        }
                    }
                    @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                    }
                });
    }

    /** Carga TODO el catálogo de ejercicios desde el servidor */
    public void listAllExercises() {
        repo.listAllExercises().enqueue(new Callback<List<EjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EjercicioDTO>> call,
                                   Response<List<EjercicioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EjercicioDTO> exercisesList = response.body();
                    allExercises.setValue(exercisesList);
                }
            }
            @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
            }
        });
    }

    public void deleteExercise(int ejercicioId, Runnable onSuccess, Runnable onError) {
        repo.deleteExercise(ejercicioId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                onError.run();
            }
        });
    }

    public void updateExercise(EjercicioDTO ejercicio, File imagen,
                               Runnable onSuccess, Runnable onError) {
        repo.createOrUpdateExercise(ejercicio, imagen)
                .enqueue(new Callback<EjercicioDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<EjercicioDTO> call,
                                           @NonNull Response<EjercicioDTO> response) {
                        if (response.isSuccessful()) {
                            onSuccess.run();
                        } else {
                            onError.run();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<EjercicioDTO> call, @NonNull Throwable t) {
                        onError.run();
                    }
                });
    }

}
