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

public class SeriesViewModel extends AndroidViewModel {
    private final SeriesRepository repository;

    private final MutableLiveData<List<SerieDTO>> _seriesList = new MutableLiveData<>();
    private final MutableLiveData<SerieDTO> _serieDetail = new MutableLiveData<>();

    public SeriesViewModel(@NonNull Application application) {
        super(application);
        repository = new SeriesRepository(application.getApplicationContext());
    }

    public LiveData<List<SerieDTO>> getSeriesList() {
        return _seriesList;
    }

    public LiveData<SerieDTO> getSerieDetail() {
        return _serieDetail;
    }

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
                _seriesList.setValue(null); //
            }
        });
    }

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

            }
        });
    }

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

            }
        });
    }

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

            }
        });
    }
}
