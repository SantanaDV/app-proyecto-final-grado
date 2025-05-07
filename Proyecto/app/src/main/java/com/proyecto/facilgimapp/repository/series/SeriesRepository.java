package com.proyecto.facilgimapp.repository.series;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class SeriesRepository {
    private final ApiService apiService;

    public SeriesRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<List<SerieDTO>> listSeries(int relacionId) {
        return apiService.listSeries(relacionId);
    }

    public Call<SerieDTO> getSeries(int id) {
        return apiService.getSeries(id);
    }

    public Call<SerieDTO> createSeries(SerieDTO dto) {
        return apiService.createSeries(dto);
    }

    public Call<SerieDTO> updateSeries(int id, SerieDTO dto) {
        return apiService.updateSeries(id, dto);
    }

    public Call<Void> deleteSeries(int id) {
        return apiService.deleteSeries(id);
    }
}
