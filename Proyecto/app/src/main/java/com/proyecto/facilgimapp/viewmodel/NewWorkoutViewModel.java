package com.proyecto.facilgimapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * ViewModel que mantiene el estado de un nuevo entrenamiento en construcción
 * mientras el usuario rellena el formulario antes de iniciar la sesión.
 * <p>
 * Guarda en LiveData:
 * <ul>
 *   <li>Un {@link EntrenamientoDTO} parcial (draft) con los campos ya completados.</li>
 *   <li>La fecha seleccionada para el entrenamiento.</li>
 *   <li>La lista de IDs de ejercicios seleccionados.</li>
 *   <li>La posición del Spinner que indica el tipo de entrenamiento.</li>
 * </ul>
 * </p>
 *
 * Autor: Francisco Santana
 */
public class NewWorkoutViewModel extends ViewModel {
    /**
     * DTO de entrenamiento en construcción. Inicializado con un objeto vacío.
     */
    private final MutableLiveData<EntrenamientoDTO> _draftWorkout = new MutableLiveData<>(new EntrenamientoDTO());
    /**
     * LiveData público para observar el {@link EntrenamientoDTO} parcial.
     *
     * @return LiveData que emite el DTO en construcción.
     */
    public LiveData<EntrenamientoDTO> draftWorkout() { return _draftWorkout; }

    /**
     * Fecha seleccionada para el entrenamiento.
     */
    private final MutableLiveData<LocalDate> _draftDate = new MutableLiveData<>();
    /**
     * LiveData público para observar la fecha seleccionada.
     *
     * @return LiveData que emite el {@link LocalDate} elegido.
     */
    public LiveData<LocalDate> draftDate() { return _draftDate; }

    /**
     * Lista de IDs de ejercicios seleccionados por el usuario.
     */
    private final MutableLiveData<List<Integer>> _draftExercises = new MutableLiveData<>();
    /**
     * LiveData público para observar los IDs de ejercicios seleccionados.
     *
     * @return LiveData que emite la lista de IDs de {@link Integer}.
     */
    public LiveData<List<Integer>> draftExercises() { return _draftExercises; }

    /**
     * Posición seleccionada en el Spinner que indica el tipo de entrenamiento.
     */
    private final MutableLiveData<Integer> _draftTypePosition = new MutableLiveData<>();
    /**
     * LiveData público para observar la posición del Spinner de tipos.
     *
     * @return LiveData que emite un {@link Integer} con el índice seleccionado.
     */
    public LiveData<Integer> draftTypePosition() { return _draftTypePosition; }

    /**
     * Actualiza el {@link EntrenamientoDTO} en construcción con los valores actuales.
     *
     * @param dto DTO completo o parcial con los datos ingresados.
     */
    public void setDraftWorkout(EntrenamientoDTO dto) {
        _draftWorkout.setValue(dto);
    }

    /**
     * Establece la fecha seleccionada para el entrenamiento.
     *
     * @param date {@link LocalDate} elegido por el usuario.
     */
    public void setDraftDate(LocalDate date) {
        _draftDate.setValue(date);
    }

    /**
     * Guarda la lista de IDs de ejercicios seleccionados.
     *
     * @param ids Lista de {@link Integer} con los IDs seleccionados.
     */
    public void setDraftExercises(List<Integer> ids) {
        _draftExercises.setValue(ids);
    }

    /**
     * Indica la posición seleccionada en el Spinner de tipos de entrenamiento.
     *
     * @param pos Índice entero de la opción seleccionada.
     */
    public void setDraftTypePosition(int pos) {
        _draftTypePosition.setValue(pos);
    }
}
