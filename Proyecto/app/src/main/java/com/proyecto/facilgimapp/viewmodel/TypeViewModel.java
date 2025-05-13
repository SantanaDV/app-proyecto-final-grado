package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.repository.TypeRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeViewModel extends AndroidViewModel {
    private final TypeRepository repository;
    private final MutableLiveData<List<TipoEntrenamientoDTO>> _types = new MutableLiveData<>();
    public LiveData<List<TipoEntrenamientoDTO>> getTypes() { return _types; }

    public TypeViewModel(@NonNull Application application) {
        super(application);
        repository = new TypeRepository(application);
    }

    public void loadTypes() {
        repository.listTypes().enqueue(new Callback<List<TipoEntrenamientoDTO>>() {
            @Override
            public void onResponse(Call<List<TipoEntrenamientoDTO>> call,
                                   Response<List<TipoEntrenamientoDTO>> response) {
                if (response.isSuccessful()) {
                    _types.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<TipoEntrenamientoDTO>> call, Throwable t) {
                _types.setValue(null); // o emitir error
            }
        });
    }

    public void addType(String nombre) {
        TipoEntrenamientoDTO dto = new TipoEntrenamientoDTO(nombre);

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

    public void updateType(int id, String nombre) {
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

    public void deleteType(int typeId, DeletionCallback callback) {
        repository.deleteType(typeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTypes(); // Actualizar lista
                    callback.onSuccess();
                } else {
                    callback.onFailure("No se puede eliminar el tipo porque está en uso.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Error al intentar eliminar el tipo: " + t.getMessage());
            }
        });
    }

    public interface DeletionCallback {
        void onSuccess();
        void onFailure(String message);
    }
}
