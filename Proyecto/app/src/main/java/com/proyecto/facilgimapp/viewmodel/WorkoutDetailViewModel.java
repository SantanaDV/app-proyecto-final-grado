package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    public void loadDetails(int userId) {
        // Ahora usamos getWorkoutsByUserId que devuelve una lista de entrenamientos
        workoutRepo.getWorkoutsByUserId(userId).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoDTO>> call, Response<List<EntrenamientoDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // En este caso seleccionamos el primer entrenamiento de la lista
                    // Aquí asumiríamos que la lista tiene al menos un entrenamiento
                    _workout.setValue(response.body().get(0)); // Asignamos el primer entrenamiento de la lista
                } else {
                    errorMessage.setValue("Error al cargar los entrenamientos del usuario");
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoDTO>> call, Throwable t) {
                errorMessage.setValue("Error al cargar los entrenamientos: " + t.getMessage());
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
                    errorMessage.setValue("Error al cargar los ejercicios del entrenamiento");
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoEjercicioDTO>> call, Throwable t) {
                errorMessage.setValue("Error al cargar los ejercicios del entrenamiento: " + t.getMessage());
            }
        });
    }
}
