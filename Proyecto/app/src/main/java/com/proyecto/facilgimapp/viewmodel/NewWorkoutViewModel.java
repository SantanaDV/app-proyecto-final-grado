package com.proyecto.facilgimapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;

import java.time.LocalDate;
import java.util.List;

public class NewWorkoutViewModel extends ViewModel {
    // DTO en construcción
    private final MutableLiveData<EntrenamientoDTO> _draftWorkout = new MutableLiveData<>(new EntrenamientoDTO());
    public LiveData<EntrenamientoDTO> draftWorkout() { return _draftWorkout; }

    // Fecha seleccionada
    private final MutableLiveData<LocalDate> _draftDate = new MutableLiveData<>();
    public LiveData<LocalDate> draftDate() { return _draftDate; }

    // Lista de IDs de ejercicios seleccionados
    private final MutableLiveData<List<Integer>> _draftExercises = new MutableLiveData<>();
    public LiveData<List<Integer>> draftExercises() { return _draftExercises; }

    // Posición de spinner seleccionada (índice)
    private final MutableLiveData<Integer> _draftTypePosition = new MutableLiveData<>();
    public LiveData<Integer> draftTypePosition() { return _draftTypePosition; }

    // Setters para guardar el estado
    public void setDraftWorkout(EntrenamientoDTO dto) {
        _draftWorkout.setValue(dto);
    }

    public void setDraftDate(LocalDate date) {
        _draftDate.setValue(date);
    }

    public void setDraftExercises(List<Integer> ids) {
        _draftExercises.setValue(ids);
    }

    public void setDraftTypePosition(int pos) {
        _draftTypePosition.setValue(pos);
    }
}
