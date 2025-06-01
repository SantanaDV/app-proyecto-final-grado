package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<EntrenamientoDTO> _workout = new MutableLiveData<>();
    private final MutableLiveData<List<EntrenamientoEjercicioDTO>> _relations = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final WorkoutRepository workoutRepo;
    private final TrainingExerciseRepository relationRepo;

    public WorkoutDetailViewModel(@NonNull Application app) {
        super(app);
        workoutRepo  = new WorkoutRepository(app.getApplicationContext());
        relationRepo = new TrainingExerciseRepository(app.getApplicationContext());
    }

    public LiveData<EntrenamientoDTO> getWorkout() {
        return _workout;
    }

    public LiveData<List<EntrenamientoEjercicioDTO>> getRelations() {
        return _relations;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /** Carga los detalles de un entrenamiento por el ID del usuario */
    public void loadDetails(int entrenamientoId) {
        workoutRepo.getWorkout(entrenamientoId).enqueue(new Callback<Entrenamiento>() {
            @Override
            public void onResponse(Call<Entrenamiento> call, Response<Entrenamiento> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Entrenamiento entrenamiento = response.body();
                    EntrenamientoDTO dto = new EntrenamientoDTO(entrenamiento);
                    _workout.setValue(dto);
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_cargar_entrenamiento));
                }
            }

            @Override
            public void onFailure(Call<Entrenamiento> call, Throwable t) {
                errorMessage.setValue(R.string.error_cargar_entrenamiento+": " + t.getMessage());
            }
        });
    }

    /** Carga la lista de relaciones entre entrenamiento y ejercicios */
    public void loadRelations(int workoutId) {
        relationRepo.listExercisesForWorkout(workoutId).enqueue(new Callback<List<EntrenamientoEjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoEjercicioDTO>> call, Response<List<EntrenamientoEjercicioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _relations.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_cargar_ejercicios_entrenamiento));
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoEjercicioDTO>> call, Throwable t) {
                errorMessage.setValue(R.string.error_cargar_ejercicios_entrenamiento+": " + t.getMessage());
            }
        });
    }
}
