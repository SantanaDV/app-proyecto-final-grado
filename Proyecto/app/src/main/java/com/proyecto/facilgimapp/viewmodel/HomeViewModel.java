package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.repository.EjercicioRepository;
import com.proyecto.facilgimapp.repository.TypeRepository;
import com.proyecto.facilgimapp.repository.WorkoutRepository;
import com.proyecto.facilgimapp.util.SessionManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final WorkoutRepository workoutRepo;
    private final EjercicioRepository ejercicioRepo;
    private final TypeRepository typeRepo;

    private final MutableLiveData<Integer> totalExercises = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalWorkouts = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalTypes = new MutableLiveData<>(0);
    private final MutableLiveData<List<EjercicioDTO>> latestExercises = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<LocalDate>> workoutDates = new MutableLiveData<>(Collections.emptyList());

    public HomeViewModel(@NonNull Application application) {
        super(application);
        workoutRepo = new WorkoutRepository(application);
        ejercicioRepo = new EjercicioRepository(application);
        typeRepo = new TypeRepository(application);
    }

    public LiveData<Integer> getTotalExercises() { return totalExercises; }
    public LiveData<Integer> getTotalWorkouts()  { return totalWorkouts;  }
    public LiveData<Integer> getTotalTypes()     { return totalTypes;     }
    public MutableLiveData<List<EjercicioDTO>> getLatestExercises() { return latestExercises; }
    public LiveData<List<LocalDate>> getWorkoutDates() { return workoutDates; }

    public void loadData() {
        //  Ejercicios y últimos
        ejercicioRepo.listAllExercises().enqueue(new Callback<List<EjercicioDTO>>() {
            @Override public void onResponse(Call<List<EjercicioDTO>> call, Response<List<EjercicioDTO>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    List<EjercicioDTO> all = r.body();
                    totalExercises.setValue(all.size());
                    int start = Math.max(0, all.size() - 5);
                    latestExercises.setValue(all.subList(start, all.size()));
                } else {
                    totalExercises.setValue(0);
                    latestExercises.setValue(Collections.emptyList());
                }
            }
            @Override public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                totalExercises.setValue(0);
                latestExercises.setValue(Collections.emptyList());
            }
        });

        //  Entrenamientos y fechas para calendario
        workoutRepo.getWorkoutsByUserId(SessionManager.getUserId(this.getApplication().getApplicationContext())).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override public void onResponse(Call<List<EntrenamientoDTO>> call, Response<List<EntrenamientoDTO>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    List<EntrenamientoDTO> list = r.body();
                    totalWorkouts.setValue(list.size());
                    // parse fechas
                    List<LocalDate> dates = list.stream()
                            .map(EntrenamientoDTO::getFechaEntrenamiento)  
                            .collect(Collectors.toList());
                    workoutDates.setValue(dates);
                } else {
                    totalWorkouts.setValue(0);
                    workoutDates.setValue(Collections.emptyList());
                }
            }
            @Override public void onFailure(Call<List<EntrenamientoDTO>> call, Throwable t) {
                totalWorkouts.setValue(0);
                workoutDates.setValue(Collections.emptyList());
            }
        });

        // Tipos de entrenamiento
        typeRepo.listTypes().enqueue(new Callback<List<TipoEntrenamientoDTO>>() {
            @Override public void onResponse(Call<List<TipoEntrenamientoDTO>> call, Response<List<TipoEntrenamientoDTO>> r) {
                totalTypes.setValue(r.isSuccessful() && r.body()!=null ? r.body().size() : 0);
            }
            @Override public void onFailure(Call<List<TipoEntrenamientoDTO>> call, Throwable t) {
                totalTypes.setValue(0);
            }
        });
    }
}
