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

/**
 * ViewModel encargado de gestionar la carga, actualización y eliminación
 * de ejercicios desde el backend mediante {@link EjercicioRepository}.
 * <p>
 * Proporciona LiveData para:
 * <ul>
 *   <li>Listado de ejercicios asociados a un entrenamiento específico.</li>
 *   <li>Catálogo completo de ejercicios disponibles.</li>
 * </ul>
 * Además, ofrece métodos para borrar o crear/actualizar ejercicios,
 * notificando éxito o error mediante callbacks.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class ExercisesViewModel extends AndroidViewModel {
    private final EjercicioRepository repo;

    /**
     * LiveData que contiene los ejercicios filtrados por un entrenamiento dado.
     */
    private final MutableLiveData<List<EjercicioDTO>> exercises = new MutableLiveData<>();

    /**
     * LiveData que contiene el catálogo completo de ejercicios.
     */
    private final MutableLiveData<List<EjercicioDTO>> allExercises = new MutableLiveData<>();

    /**
     * Constructor que inicializa el repositorio de ejercicios.
     *
     * @param application Aplicación, necesaria para el contexto dentro del repositorio.
     */
    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        repo = new EjercicioRepository(application);
    }

    /**
     * Obtiene los ejercicios asociados al entrenamiento actual.
     *
     * @return LiveData con lista de {@link EjercicioDTO} filtrados.
     */
    public LiveData<List<EjercicioDTO>> getExercises() {
        return exercises;
    }

    /**
     * Obtiene el catálogo completo de ejercicios disponibles.
     *
     * @return LiveData con lista de todos los {@link EjercicioDTO}.
     */
    public LiveData<List<EjercicioDTO>> getAllExercises() {
        return allExercises;
    }

    /**
     * Carga la lista de ejercicios asociados a un entrenamiento específico
     * identificado por {@code trainingId} y el nombre de usuario.
     * <p>
     * Al recibir la respuesta, actualiza {@link #exercises} si la llamada es exitosa.
     * </p>
     *
     * @param trainingId Identificador del entrenamiento.
     * @param username   Nombre de usuario para filtrar los ejercicios.
     */
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
                    // En caso de error, no se actualiza el LiveData
                }
            });
    }

    /**
     * Solicita al repositorio el catálogo completo de ejercicios desde el servidor.
     * <p>
     * Al recibir la respuesta, actualiza {@link #allExercises} si es exitosa.
     * </p>
     */
    public void listAllExercises() {
        repo.listAllExercises().enqueue(new Callback<List<EjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EjercicioDTO>> call,
                                   Response<List<EjercicioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allExercises.setValue(response.body());
                }
            }
            @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                // En caso de error, no se actualiza el LiveData
            }
        });
    }

    /**
     * Elimina un ejercicio identificado por {@code ejercicioId}.
     * <p>
     * Ejecuta {@code onSuccess} si la eliminación fue correcta,
     * o {@code onError} en caso contrario (fallo HTTP o de red).
     * </p>
     *
     * @param ejercicioId ID del ejercicio a eliminar.
     * @param onSuccess   Runnable que se ejecuta al eliminar con éxito.
     * @param onError     Runnable que se ejecuta si ocurre un error.
     */
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

    /**
     * Crea o actualiza un ejercicio en el servidor, enviando el objeto
     * {@link EjercicioDTO} y un archivo de imagen opcional.
     * <p>
     * Ejecuta {@code onSuccess} si la llamada fue exitosa,
     * o {@code onError} en caso de fallo (HTTP o red).
     * </p>
     *
     * @param ejercicio  DTO con los datos del ejercicio a crear o actualizar.
     * @param imagen     Archivo de imagen, puede ser null si no se desea actualizar.
     * @param onSuccess  Runnable que se ejecuta si la operación es exitosa.
     * @param onError    Runnable que se ejecuta si ocurre un error.
     */
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
                @Override public void onFailure(@NonNull Call<EjercicioDTO> call, @NonNull Throwable t) {
                    onError.run();
                }
            });
    }
}
