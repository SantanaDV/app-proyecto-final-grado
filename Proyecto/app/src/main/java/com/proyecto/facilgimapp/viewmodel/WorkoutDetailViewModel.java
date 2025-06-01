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

/**
 * ViewModel responsable de cargar y exponer los detalles de un entrenamiento
 * específico y sus relaciones con ejercicios (EntrenamientoEjercicioDTO).
 * <p>
 * Utiliza {@link WorkoutRepository} para obtener los datos del entrenamiento
 * y {@link TrainingExerciseRepository} para las relaciones entrenamiento–ejercicio.
 * Los resultados se publican en LiveData para que la interfaz de usuario los observe.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class WorkoutDetailViewModel extends AndroidViewModel {

    /**
     * LiveData que contiene el DTO con los datos del entrenamiento.
     */
    private final MutableLiveData<EntrenamientoDTO> _workout = new MutableLiveData<>();

    /**
     * LiveData que contiene la lista de relaciones {@link EntrenamientoEjercicioDTO}
     * entre el entrenamiento y sus ejercicios.
     */
    private final MutableLiveData<List<EntrenamientoEjercicioDTO>> _relations = new MutableLiveData<>();

    /**
     * LiveData que contiene mensajes de error en caso de fallo al cargar datos.
     */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final WorkoutRepository workoutRepo;
    private final TrainingExerciseRepository relationRepo;

    /**
     * Constructor que inicializa los repositorios necesarios para obtener datos.
     *
     * @param app Contexto de la aplicación para construir los repositorios.
     */
    public WorkoutDetailViewModel(@NonNull Application app) {
        super(app);
        workoutRepo = new WorkoutRepository(app.getApplicationContext());
        relationRepo = new TrainingExerciseRepository(app.getApplicationContext());
    }

    /**
     * Proporciona el LiveData que emite el {@link EntrenamientoDTO} cargado.
     *
     * @return LiveData con el DTO del entrenamiento.
     */
    public LiveData<EntrenamientoDTO> getWorkout() {
        return _workout;
    }

    /**
     * Proporciona el LiveData que emite la lista de relaciones entre entrenamiento
     * y ejercicios.
     *
     * @return LiveData con lista de {@link EntrenamientoEjercicioDTO}.
     */
    public LiveData<List<EntrenamientoEjercicioDTO>> getRelations() {
        return _relations;
    }

    /**
     * Proporciona el LiveData que emite mensajes de error cuando algo falla
     * al cargar datos del entrenamiento o sus relaciones.
     *
     * @return LiveData con el mensaje de error.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Carga los detalles de un entrenamiento desde el servidor, usando su ID.
     * <p>
     * Si la llamada es exitosa y retorna un objeto {@link Entrenamiento},
     * se convierte a {@link EntrenamientoDTO} y se publica en {@link #_workout}.
     * En caso de error HTTP o de red, se publica un mensaje de error en {@link #errorMessage}.
     * </p>
     *
     * @param entrenamientoId ID del entrenamiento que se desea cargar.
     */
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
                errorMessage.setValue(
                        getApplication().getString(R.string.error_cargar_entrenamiento) + ": " + t.getMessage()
                );
            }
        });
    }

    /**
     * Carga la lista de relaciones (EntrenamientoEjercicioDTO) para un entrenamiento dado.
     * <p>
     * Si la llamada es exitosa, publica la lista en {@link #_relations}.
     * En caso de error HTTP o de red, publica un mensaje en {@link #errorMessage}.
     * </p>
     *
     * @param workoutId ID del entrenamiento cuyas relaciones se desean cargar.
     */
    public void loadRelations(int workoutId) {
        relationRepo.listExercisesForWorkout(workoutId).enqueue(new Callback<List<EntrenamientoEjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoEjercicioDTO>> call,
                                   Response<List<EntrenamientoEjercicioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _relations.setValue(response.body());
                } else {
                    errorMessage.setValue(
                            getApplication().getString(R.string.error_cargar_ejercicios_entrenamiento)
                    );
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoEjercicioDTO>> call, Throwable t) {
                errorMessage.setValue(
                        getApplication().getString(R.string.error_cargar_ejercicios_entrenamiento)
                                + ": " + t.getMessage()
                );
            }
        });
    }
}
