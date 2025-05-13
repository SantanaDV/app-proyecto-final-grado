package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class TypeRepository {
    private final ApiService apiService;

    public TypeRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<List<TipoEntrenamientoDTO>> listTypes() {
        return apiService.listTypes();
    }

    public Call<TipoEntrenamientoDTO> createType(TipoEntrenamientoDTO dto) {
        return apiService.createType(dto);
    }

    public Call<TipoEntrenamientoDTO> updateType(int id, TipoEntrenamientoDTO dto) {
        return apiService.updateType(id, dto);
    }

    public Call<Void> deleteType(int id) {
        return apiService.deleteType(id);
    }
    public Call<TipoEntrenamientoDTO> getType(int id) {
        return apiService.getType(id);
    }
}
