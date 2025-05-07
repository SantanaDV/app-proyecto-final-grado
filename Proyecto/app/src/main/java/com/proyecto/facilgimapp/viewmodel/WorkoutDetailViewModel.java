package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.repository.workout.TrainingExerciseRepository;
import com.proyecto.facilgimapp.util.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailViewModel extends AndroidViewModel {
    private final TrainingExerciseRepository repo;
    public final MutableLiveData<List<EjercicioDTO>> exercises = new MutableLiveData<>();

    public WorkoutDetailViewModel(@NonNull Application application) {
        super(application);
        repo = new TrainingExerciseRepository(application.getApplicationContext());
    }

    /** Carga los ejercicios del entrenamiento para el usuario logueado */
    public void load(int workoutId) {
        String username = SessionManager.getUsername(getApplication());
        repo.fetchExercisesForWorkout(workoutId, username, new Callback<List<EjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EjercicioDTO>> call,
                                   Response<List<EjercicioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    exercises.setValue(response.body());
                }
            }
            @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                // Aquí podrías setValue(Collections.emptyList()) o loggear
            }
        });
    }
}
