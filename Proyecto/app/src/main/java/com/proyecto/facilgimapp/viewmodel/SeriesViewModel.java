package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.repository.SeriesRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel que gestiona las operaciones CRUD de series asociadas a un entrenamiento
 * o relación específica. Utiliza {@link SeriesRepository} para comunicarse con el backend.
 * Expone LiveData para observar la lista de series y el detalle de una serie individual.
 * <p>
 * Autor: Francisco Santana
 * </p>
 */
public class SeriesViewModel extends AndroidViewModel {
    /**
     * Repositorio que realiza llamadas a la API REST para series.
     */
    private final SeriesRepository repository;

    /**
     * LiveData que contiene la lista de {@link SerieDTO} asociadas a una relación.
     */
    private final MutableLiveData<List<SerieDTO>> _seriesList = new MutableLiveData<>();

    /**
     * LiveData que contiene el detalle de una sola {@link SerieDTO}.
     */
    private final MutableLiveData<SerieDTO> _serieDetail = new MutableLiveData<>();

    /**
     * Constructor que inicializa el repositorio a partir del contexto de la aplicación.
     *
     * @param application Aplicación actual, usada para obtener el contexto.
     */
    public SeriesViewModel(@NonNull Application application) {
        super(application);
        repository = new SeriesRepository(application.getApplicationContext());
    }

    /**
     * Proporciona un LiveData para observar la lista de series de una relación.
     *
     * @return LiveData con la lista de {@link SerieDTO}.
     */
    public LiveData<List<SerieDTO>> getSeriesList() {
        return _seriesList;
    }

    /**
     * Proporciona un LiveData para observar el detalle de una serie específica.
     *
     * @return LiveData con el {@link SerieDTO} detallado.
     */
    public LiveData<SerieDTO> getSerieDetail() {
        return _serieDetail;
    }

    /**
     * Carga todas las series asociadas a la relación identificada por {@code relacionId}.
     * Actualiza {@link #_seriesList} con el resultado; en caso de fallo, se envía null.
     *
     * @param relacionId ID de la relación (por ejemplo, entrenamiento-ejercicio) cuyas series se desean cargar.
     */
    public void loadSeries(int relacionId) {
        repository.listSeries(relacionId).enqueue(new Callback<List<SerieDTO>>() {
            @Override
            public void onResponse(Call<List<SerieDTO>> call, Response<List<SerieDTO>> response) {
                if (response.isSuccessful()) {
                    _seriesList.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<SerieDTO>> call, Throwable t) {
                _seriesList.setValue(null);
            }
        });
    }

    /**
     * Carga el detalle de una serie concreta identificada por {@code id}.
     * Actualiza {@link #_serieDetail} con el resultado; en caso de fallo, se envía null.
     *
     * @param id ID de la serie a recuperar.
     */
    public void loadSeriesDetail(int id) {
        repository.getSeries(id).enqueue(new Callback<SerieDTO>() {
            @Override
            public void onResponse(Call<SerieDTO> call, Response<SerieDTO> response) {
                if (response.isSuccessful()) {
                    _serieDetail.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<SerieDTO> call, Throwable t) {
                _serieDetail.setValue(null);
            }
        });
    }

    /**
     * Crea una nueva serie en el backend usando los datos de {@code dto}. 
     * Tras la creación exitosa, recarga todas las series de la relación {@code relacionId}.
     *
     * @param dto         DTO con los datos de la nueva serie a crear.
     * @param relacionId  ID de la relación a la que pertenece la serie.
     */
    public void addSeries(SerieDTO dto, int relacionId) {
        repository.createSeries(dto).enqueue(new Callback<SerieDTO>() {
            @Override
            public void onResponse(Call<SerieDTO> call, Response<SerieDTO> response) {
                if (response.isSuccessful()) {
                    loadSeries(relacionId);
                }
            }
            @Override
            public void onFailure(Call<SerieDTO> call, Throwable t) {
                // No se actualiza si hay error
            }
        });
    }

    /**
     * Actualiza una serie existente en el backend con los datos de {@code dto}. 
     * Tras la actualización exitosa, recarga todas las series de la relación {@code relacionId}.
     *
     * @param id          ID de la serie a actualizar.
     * @param dto         DTO con los datos actualizados de la serie.
     * @param relacionId  ID de la relación a la que pertenece la serie.
     */
    public void updateSeries(int id, SerieDTO dto, int relacionId) {
        repository.updateSeries(id, dto).enqueue(new Callback<SerieDTO>() {
            @Override
            public void onResponse(Call<SerieDTO> call, Response<SerieDTO> response) {
                if (response.isSuccessful()) {
                    loadSeries(relacionId);
                }
            }
            @Override
            public void onFailure(Call<SerieDTO> call, Throwable t) {
                // No se actualiza si hay error
            }
        });
    }

    /**
     * Elimina la serie identificada por {@code id} en el backend.
     * Tras la eliminación exitosa, recarga todas las series de la relación {@code relacionId}.
     *
     * @param id          ID de la serie a eliminar.
     * @param relacionId  ID de la relación a la que pertenece la serie.
     */
    public void deleteSeries(int id, int relacionId) {
        repository.deleteSeries(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadSeries(relacionId);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // No se actualiza si hay error
            }
        });
    }
}
