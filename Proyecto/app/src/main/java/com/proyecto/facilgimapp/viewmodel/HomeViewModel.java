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

/**
 * ViewModel que provee los datos estadísticos e informativos para la pantalla de inicio (Home).
 * <p>
 * Carga:
 * <ul>
 *   <li>Total de ejercicios registrados.</li>
 *   <li>Últimos 5 ejercicios añadidos.</li>
 *   <li>Total de entrenamientos del usuario y las fechas para el calendario.</li>
 *   <li>Total de tipos de entrenamiento disponibles.</li>
 * </ul>
 * Expone {@link LiveData} que la UI observa para actualizar contadores y vistas.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class HomeViewModel extends AndroidViewModel {
    private final WorkoutRepository workoutRepo;
    private final EjercicioRepository ejercicioRepo;
    private final TypeRepository typeRepo;

    /**
     * Total de ejercicios existentes en la base de datos.
     */
    private final MutableLiveData<Integer> totalExercises = new MutableLiveData<>(0);

    /**
     * Total de entrenamientos del usuario actual.
     */
    private final MutableLiveData<Integer> totalWorkouts = new MutableLiveData<>(0);

    /**
     * Total de tipos de entrenamiento definidos.
     */
    private final MutableLiveData<Integer> totalTypes = new MutableLiveData<>(0);

    /**
     * Lista con los últimos 5 ejercicios añadidos (o menos si no hay tantos).
     */
    private final MutableLiveData<List<EjercicioDTO>> latestExercises =
            new MutableLiveData<>(Collections.emptyList());

    /**
     * Lista de fechas de entrenamientos para decorar el calendario (fechas de cada entrenamiento del usuario).
     */
    private final MutableLiveData<List<LocalDate>> workoutDates =
            new MutableLiveData<>(Collections.emptyList());

    /**
     * Constructor que inicializa los repositorios necesarios para cargar datos.
     *
     * @param application Objeto Application para obtener contexto si es necesario.
     */
    public HomeViewModel(@NonNull Application application) {
        super(application);
        workoutRepo = new WorkoutRepository(application);
        ejercicioRepo = new EjercicioRepository(application);
        typeRepo = new TypeRepository(application);
    }

    /**
     * LiveData que contiene el número total de ejercicios.
     *
     * @return LiveData con un Integer que representa el total de ejercicios.
     */
    public LiveData<Integer> getTotalExercises() {
        return totalExercises;
    }

    /**
     * LiveData que contiene el número total de entrenamientos del usuario.
     *
     * @return LiveData con un Integer que representa el total de entrenamientos.
     */
    public LiveData<Integer> getTotalWorkouts() {
        return totalWorkouts;
    }

    /**
     * LiveData que contiene el número total de tipos de entrenamiento.
     *
     * @return LiveData con un Integer que representa el total de tipos.
     */
    public LiveData<Integer> getTotalTypes() {
        return totalTypes;
    }

    /**
     * LiveData que contiene la lista de los últimos ejercicios añadidos.
     *
     * @return MutableLiveData con List<EjercicioDTO> de los últimos ejercicios.
     */
    public MutableLiveData<List<EjercicioDTO>> getLatestExercises() {
        return latestExercises;
    }

    /**
     * LiveData que contiene las fechas de todos los entrenamientos del usuario
     * para mostrar en el calendario.
     *
     * @return LiveData con List<LocalDate> de fechas de entrenamientos.
     */
    public LiveData<List<LocalDate>> getWorkoutDates() {
        return workoutDates;
    }

    /**
     * Carga todos los datos necesarios para la pantalla de inicio:
     * <ul>
     *   <li>Consulta todos los ejercicios para actualizar {@link #totalExercises} y {@link #latestExercises}.</li>
     *   <li>Consulta los entrenamientos del usuario actual para actualizar {@link #totalWorkouts}
     *       y extraer fechas en {@link #workoutDates}.</li>
     *   <li>Consulta todos los tipos de entrenamiento para actualizar {@link #totalTypes}.</li>
     * </ul>
     * Cada petición es asíncrona; en caso de error HTTP o de red, se asignan valores por defecto (0 ó lista vacía).
     */
    public void loadData() {
        // 1) Cargar ejercicios y determinar últimos 5
        ejercicioRepo.listAllExercises().enqueue(new Callback<List<EjercicioDTO>>() {
            @Override
            public void onResponse(Call<List<EjercicioDTO>> call, Response<List<EjercicioDTO>> r) {
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

            @Override
            public void onFailure(Call<List<EjercicioDTO>> call, Throwable t) {
                totalExercises.setValue(0);
                latestExercises.setValue(Collections.emptyList());
            }
        });

        // 2) Cargar entrenamientos del usuario y extraer fechas
        int userId = SessionManager.getUserId(getApplication().getApplicationContext());
        workoutRepo.getWorkoutsByUserId(userId).enqueue(new Callback<List<EntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenamientoDTO>> call,
                                   Response<List<EntrenamientoDTO>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    List<EntrenamientoDTO> list = r.body();
                    totalWorkouts.setValue(list.size());
                    // Convertir cada DTO a su fecha para el calendario
                    List<LocalDate> dates = list.stream()
                            .map(EntrenamientoDTO::getFechaEntrenamiento)
                            .collect(Collectors.toList());
                    workoutDates.setValue(dates);
                } else {
                    totalWorkouts.setValue(0);
                    workoutDates.setValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<EntrenamientoDTO>> call, Throwable t) {
                totalWorkouts.setValue(0);
                workoutDates.setValue(Collections.emptyList());
            }
        });

        // 3) Cargar tipos de entrenamiento
        typeRepo.listTypes().enqueue(new Callback<List<TipoEntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<TipoEntrenamientoDTO>> call,
                                   Response<List<TipoEntrenamientoDTO>> r) {
                totalTypes.setValue(r.isSuccessful() && r.body() != null ? r.body().size() : 0);
            }

            @Override
            public void onFailure(Call<List<TipoEntrenamientoDTO>> call, Throwable t) {
                totalTypes.setValue(0);
            }
        });
    }
}
