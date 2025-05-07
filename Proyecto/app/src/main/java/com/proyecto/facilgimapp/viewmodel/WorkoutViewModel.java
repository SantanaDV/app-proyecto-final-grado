package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.Entrenamiento;
import com.proyecto.facilgimapp.repository.workout.WorkoutRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutViewModel extends AndroidViewModel {
    private final WorkoutRepository repo;
    private final MutableLiveData<List<Entrenamiento>> workouts = new MutableLiveData<>();

    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        repo = new WorkoutRepository(application.getApplicationContext());
    }

    public LiveData<List<Entrenamiento>> getWorkouts() {
        return workouts;
    }

    public void loadWorkouts() {
        repo.getWorkouts().enqueue(new Callback<List<Entrenamiento>>() {
            @Override
            public void onResponse(Call<List<Entrenamiento>> call,
                                   Response<List<Entrenamiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workouts.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Entrenamiento>> call, Throwable t) {
            }
        });
    }
}
