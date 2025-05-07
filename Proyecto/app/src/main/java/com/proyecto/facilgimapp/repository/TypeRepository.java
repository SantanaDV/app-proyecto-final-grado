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

    public Call<TipoEntrenamientoDTO> updateType(Long id, TipoEntrenamientoDTO dto) {
        return apiService.updateType(id, dto);
    }

    public Call<Void> deleteType(Long id) {
        return apiService.deleteType(id);
    }
}
