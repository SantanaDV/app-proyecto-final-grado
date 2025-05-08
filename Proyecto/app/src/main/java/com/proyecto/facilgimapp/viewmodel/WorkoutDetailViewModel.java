package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.network.RetrofitClient;
import com.proyecto.facilgimapp.repository.workout.TrainingExerciseRepository;
import com.proyecto.facilgimapp.repository.workout.WorkoutRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailViewModel extends AndroidViewModel {
    public final MutableLiveData<Entrenamiento> workout   = new MutableLiveData<>();
    public final MutableLiveData<List<EntrenamientoEjercicioDTO>> relations = new MutableLiveData<>();
    private final WorkoutRepository workoutRepo;
    private final TrainingExerciseRepository relationRepo;

    public WorkoutDetailViewModel(@NonNull Application app) {
        super(app);
        workoutRepo  = new WorkoutRepository(app.getApplicationContext());
        relationRepo = new TrainingExerciseRepository(app.getApplicationContext());
    }

    /** Carga los datos básicos del entrenamiento */
    public void loadDetails(int id) {
        workoutRepo.getWorkout(id).enqueue(new Callback<Entrenamiento>() {
            @Override
            public void onResponse(Call<Entrenamiento> c, Response<Entrenamiento> r) {
                if (r.isSuccessful() && r.body() != null) {
                    workout.setValue(r.body());
                }
            }
            @Override public void onFailure(Call<Entrenamiento> c, Throwable t) { }
        });
    }

    /** Carga la lista ejercicio–serie */
    public void loadRelations(int workoutId) {
        relationRepo.listExercisesForWorkout(workoutId)
                .enqueue(new Callback<List<EntrenamientoEjercicioDTO>>() {
                    @Override
                    public void onResponse(Call<List<EntrenamientoEjercicioDTO>> c,
                                           Response<List<EntrenamientoEjercicioDTO>> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            relations.setValue(r.body());
                        }
                    }
                    @Override public void onFailure(Call<List<EntrenamientoEjercicioDTO>> c, Throwable t) { }
                });
    }
}
