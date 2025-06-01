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

/**
 * ViewModel que gestiona la lista de entrenamientos del usuario,
 * así como las operaciones de eliminación, actualización y obtención de relaciones
 * entre entrenamientos y ejercicios.
 * <p>
 * Expone LiveData para que la interfaz observe cambios en la lista de entrenamientos
 * y mensajes de error.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class WorkoutViewModel extends AndroidViewModel {
    /**
     * Repositorio para operaciones de entrenamientos (CRUD).
     */
    private final WorkoutRepository repo;

    /**
     * LiveData que contiene la lista de EntrenamientoDTO para el usuario actual.
     */
    private final MutableLiveData<List<EntrenamientoDTO>> _workouts = new MutableLiveData<>();

    /**
     * LiveData que contiene mensajes de error en caso de fallos en las operaciones.
     */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Constructor que inicializa el repositorio usando el contexto de la aplicación.
     *
     * @param application Aplicación actual, necesaria para construir el repositorio.
     */
    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        repo = new WorkoutRepository(application.getApplicationContext());
    }

    /**
     * Proporciona un LiveData que emite la lista de entrenamientos cargada para el usuario.
     *
     * @return LiveData con List<EntrenamientoDTO> obtenidos del servidor.
     */
    public LiveData<List<EntrenamientoDTO>> getWorkouts() {
        return _workouts;
    }

    /**
     * Proporciona un LiveData que emite mensajes de error si ocurre algún fallo
     * al realizar operaciones de carga, eliminación o actualización de entrenamientos.
     *
     * @return LiveData con un mensaje de error en formato String.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Carga del servidor todos los entrenamientos asociados al usuario cuyo ID se recibe.
     * <p>
     * Si la llamada es exitosa y retorna al menos un entrenamiento,
     * publica la lista en {@link #_workouts}. Si la lista está vacía o ocurre un
     * fallo HTTP, se asigna una lista vacía y se publica un mensaje de error.
     * </p>
     *
     * @param userId ID del usuario cuyas sesiones de entrenamiento se desean obtener.
     */
    public void loadWorkoutsByUserId(int userId) {
        repo.getWorkoutsByUserId(userId).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoDTO>> call,
                                   Response<List<EntrenamientoDTO>> response) {
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
                errorMessage.setValue(
                        getApplication().getString(R.string.error_cargar_entrenamientos)
                                + ": " + t.getMessage()
                );
            }
        });
    }

    /**
     * Elimina el entrenamiento identificado por {@code workoutId} en el servidor.
     * <p>
     * Si la eliminación es exitosa, ejecuta {@code onSuccess.run()}. En caso contrario,
     * publica un mensaje de error en {@link #errorMessage} y ejecuta {@code onFailure.run()}.
     * </p>
     *
     * @param workoutId ID del entrenamiento a eliminar.
     * @param onSuccess Runnable que se ejecuta tras eliminar correctamente.
     * @param onFailure Runnable que se ejecuta si ocurre un error.
     */
    public void deleteWorkout(int workoutId, Runnable onSuccess, Runnable onFailure) {
        repo.deleteWorkout(workoutId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    errorMessage.setValue(
                            getApplication().getString(R.string.error_eliminar_entrenamiento)
                    );
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.setValue(
                        getApplication().getString(R.string.error_eliminar_entrenamiento)
                                + ": " + t.getMessage()
                );
                onFailure.run();
            }
        });
    }

    /**
     * Actualiza un entrenamiento existente en el servidor usando los datos del DTO proporcionado.
     * <p>
     * Si la actualización es exitosa, ejecuta {@code onSuccess.run()}. En caso contrario,
     * publica un mensaje de error en {@link #errorMessage} y ejecuta {@code onFailure.run()}.
     * </p>
     *
     * @param id         ID del entrenamiento a actualizar.
     * @param dto        DTO con los datos actualizados del entrenamiento.
     * @param onSuccess  Runnable que se ejecuta tras actualizar correctamente.
     * @param onFailure  Runnable que se ejecuta si ocurre un error.
     */
    public void updateWorkout(int id, EntrenamientoDTO dto, Runnable onSuccess, Runnable onFailure) {
        repo.updateWorkoutFromDto(id, dto).enqueue(new Callback<Entrenamiento>() {
            @Override
            public void onResponse(Call<Entrenamiento> call, Response<Entrenamiento> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    errorMessage.setValue(
                            getApplication().getString(R.string.error_actualizar_entrenamiento)
                    );
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Entrenamiento> call, Throwable t) {
                errorMessage.setValue(
                        getApplication().getString(R.string.error_actualizar_entrenamiento)
                                + ": " + t.getMessage()
                );
                onFailure.run();
            }
        });
    }

    /**
     * Carga las relaciones entre el entrenamiento (identificado por {@code workoutId})
     * y sus ejercicios asociados, devolviendo la lista resultante al {@code onResult}.
     * <p>
     * Si la llamada es exitosa, invoca {@code onResult.accept(listaRelaciones)}.
     * En caso de fallo, registra el error en el log de depuración.
     * </p>
     *
     * @param workoutId ID del entrenamiento cuyas relaciones se desean obtener.
     * @param onResult  Consumer que recibe la lista de {@link EntrenamientoEjercicioDTO}.
     */
    public void loadWorkoutRelations(int workoutId,
                                     Consumer<List<EntrenamientoEjercicioDTO>> onResult) {
        new TrainingExerciseRepository(getApplication().getApplicationContext())
            .listExercisesForWorkout(workoutId)
            .enqueue(new Callback<List<EntrenamientoEjercicioDTO>>() {
                @Override
                public void onResponse(Call<List<EntrenamientoEjercicioDTO>> call,
                                       Response<List<EntrenamientoEjercicioDTO>> response) {
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
