package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.repository.EjercicioRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExercisesViewModel extends AndroidViewModel {
    private final EjercicioRepository repo;
    private final MutableLiveData<List<EjercicioDTO>> exercises = new MutableLiveData<>();

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        repo = new EjercicioRepository(application);
    }

    public LiveData<List<EjercicioDTO>> getExercises() {
        return exercises;
    }

    /**
     * Carga los ejercicios de un entrenamiento para el usuario dado.
     */
    public void loadExercises(int trainingId, String username) {
        repo.listExercisesByTraining(trainingId, username)
                .enqueue(new Callback<List<EjercicioDTO>>() {
                    @Override
                    public void onResponse(Call<List<EjercicioDTO>> call,
                                           Response<List<EjercicioDTO>> response) {
                        if (response.isSuccessful()) {
                            exercises.setValue(response.body());
                        }
                    }
                    @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                        // manejar error si quieres
                    }
                });
    }
}
