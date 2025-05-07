package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.repository.TypeRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeViewModel extends AndroidViewModel {
    private final TypeRepository repository;
    private final MutableLiveData<List<TipoEntrenamientoDTO>> types = new MutableLiveData<>();

    public TypeViewModel(@NonNull Application application) {
        super(application);
        repository = new TypeRepository(application.getApplicationContext());
    }

    public MutableLiveData<List<TipoEntrenamientoDTO>> getTypes() {
        return types;
    }

    public void loadTypes() {
        repository.listTypes().enqueue(new Callback<List<TipoEntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<TipoEntrenamientoDTO>> call,
                                   Response<List<TipoEntrenamientoDTO>> response) {
                if (response.isSuccessful()) {
                    types.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<TipoEntrenamientoDTO>> call, Throwable t) {
            }
        });
    }

    public void addType(String nombre) {
        TipoEntrenamientoDTO dto = new TipoEntrenamientoDTO(null, nombre);
        repository.createType(dto).enqueue(new Callback<TipoEntrenamientoDTO>() {
            @Override
            public void onResponse(Call<TipoEntrenamientoDTO> call,
                                   Response<TipoEntrenamientoDTO> response) {
                if (response.isSuccessful()) loadTypes();
            }
            @Override
            public void onFailure(Call<TipoEntrenamientoDTO> call, Throwable t) {
            }
        });
    }

    public void updateType(Long id, String nombre) {
        TipoEntrenamientoDTO dto = new TipoEntrenamientoDTO(id, nombre);
        repository.updateType(id, dto).enqueue(new Callback<TipoEntrenamientoDTO>() {
            @Override
            public void onResponse(Call<TipoEntrenamientoDTO> call,
                                   Response<TipoEntrenamientoDTO> response) {
                if (response.isSuccessful()) loadTypes();
            }
            @Override
            public void onFailure(Call<TipoEntrenamientoDTO> call, Throwable t) {
            }
        });
    }

    public void deleteType(Long id) {
        repository.deleteType(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) loadTypes();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
