package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel encargado de guardar la sesión de entrenamiento completada por el usuario.
 * <p>
 * Se encarga de construir la lista de relaciones entre ejercicios y sus series,
 * validar campos esenciales en el DTO de entrenamiento y enviar la información al backend
 * a través de {@link WorkoutRepository}.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class WorkoutSessionViewModel extends AndroidViewModel {
    /**
     * Repositorio que provee métodos para crear entrenamientos en el servidor.
     */
    private final WorkoutRepository workoutRepo;

    /**
     * LiveData que emite mensajes de error cuando ocurre alguna falla
     * al intentar guardar la sesión de entrenamiento.
     */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Constructor que inicializa el repositorio de entrenamientos usando el contexto de la aplicación.
     *
     * @param application Aplicación actual, usada para obtener el contexto necesario.
     */
    public WorkoutSessionViewModel(@NonNull Application application) {
        super(application);
        workoutRepo = new WorkoutRepository(application.getApplicationContext());
    }

    /**
     * Proporciona un LiveData para observar posibles mensajes de error
     * cuando se guarda la sesión de entrenamiento.
     *
     * @return LiveData con un String que describe el error, o null si no hay error.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Guarda la sesión de entrenamiento completada, construyendo la relación
     * {@link EntrenamientoEjercicioDTO} entre cada ejercicio y sus series.
     * <p>
     * Validaciones previas:
     * <ul>
     *   <li>El usuario asociado a {@code workoutDTO} debe existir con ID no nulo.</li>
     *   <li>Debe haber un tipo de entrenamiento asignado en {@code workoutDTO}.</li>
     * </ul>
     * Si falta alguno de esos campos, emite un mensaje de error en {@link #errorMessage}
     * y no realiza la llamada a la API. De lo contrario, envía la petición a través de
     * {@link WorkoutRepository#createWorkout(EntrenamientoDTO)}.
     * </p>
     *
     * @param workoutDTO  DTO de entrenamiento que contiene datos generales (nombre, descripción,
     *                    fecha, tipo, usuario, etc.). Se le asignarán las relaciones ejercicio–series.
     * @param seriesMap   Mapa donde cada clave es un {@link EjercicioDTO} y el valor es
     *                    la lista de {@link SerieDTO} asociadas a ese ejercicio.
     */
    public void saveWorkoutSession(EntrenamientoDTO workoutDTO, Map<EjercicioDTO, List<SerieDTO>> seriesMap) {
        // Construye la lista de relaciones ejercicio–series
        List<EntrenamientoEjercicioDTO> relaciones = new ArrayList<>();

        int orden = 1;
        for (Map.Entry<EjercicioDTO, List<SerieDTO>> entry : seriesMap.entrySet()) {
            EntrenamientoEjercicioDTO relacion = new EntrenamientoEjercicioDTO();
            relacion.setEjercicio(entry.getKey());
            relacion.setSeries(entry.getValue());
            relacion.setOrden(orden++); // Establece el orden de aparición en la sesión
            relaciones.add(relacion);
        }

        // Asigna las relaciones construidas al DTO principal de entrenamiento
        workoutDTO.setEntrenamientosEjercicios(relaciones);

        // Validaciones adicionales: usuario y tipo de entrenamiento son obligatorios
        if (workoutDTO.getUsuario() == null
                || workoutDTO.getUsuario().getIdUsuario() == null
                || workoutDTO.getTipoEntrenamiento() == null) {
            errorMessage.setValue(
                    getApplication().getString(R.string.error_campos_vacios)
            );
            return;
        }

        // Envío de la solicitud al backend
        workoutRepo.createWorkout(workoutDTO).enqueue(new Callback<EntrenamientoDTO>() {
            @Override
            public void onResponse(Call<EntrenamientoDTO> call,
                                   Response<EntrenamientoDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    errorMessage.setValue(
                            getApplication().getString(R.string.error_guardar_entrenamiento)
                    );
                }
            }

            @Override
            public void onFailure(Call<EntrenamientoDTO> call, Throwable t) {
                errorMessage.setValue(
                        getApplication().getString(R.string.error_guardar_entrenamiento)
                                + ": " + t.getMessage()
                );
            }
        });
    }
}
