package com.proyecto.facilgimapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.repository.TypeRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel encargado de gestionar los tipos de entrenamiento.
 * <p>
 * Proporciona métodos para cargar la lista de tipos, agregar,
 * actualizar y eliminar un tipo, exponiendo los resultados en LiveData.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class TypeViewModel extends AndroidViewModel {
    /**
     * Repositorio que maneja las operaciones de API relacionadas con tipos.
     */
    private final TypeRepository repository;

    /**
     * LiveData que contiene la lista de {@link TipoEntrenamientoDTO} disponibles.
     */
    private final MutableLiveData<List<TipoEntrenamientoDTO>> _types = new MutableLiveData<>();

    /**
     * LiveData público para observar la lista de tipos de entrenamiento.
     *
     * @return LiveData con List<TipoEntrenamientoDTO>.
     */
    public LiveData<List<TipoEntrenamientoDTO>> getTypes() {
        return _types;
    }

    /**
     * Constructor que inicializa el repositorio de tipos.
     *
     * @param application Objeto Application para acceder al contexto si es necesario.
     */
    public TypeViewModel(@NonNull Application application) {
        super(application);
        repository = new TypeRepository(application);
    }

    /**
     * Carga todos los tipos de entrenamiento desde el servidor.
     * <p>
     * Al recibir la respuesta, actualiza {@link #_types} con la lista obtenida.
     * En caso de fallo, establece el valor en null.
     * </p>
     */
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
                _types.setValue(null); // o podría dejar la última lista
            }
        });
    }

    /**
     * Agrega un nuevo tipo de entrenamiento con el nombre indicado.
     * <p>
     * Construye un DTO y envía la solicitud. Si es exitoso, recarga la lista
     * de tipos llamando a {@link #loadTypes()}.
     * </p>
     *
     * @param nombre Nombre del nuevo tipo a crear.
     */
    public void addType(String nombre) {
        TipoEntrenamientoDTO dto = new TipoEntrenamientoDTO(nombre);
        repository.createType(dto).enqueue(new Callback<TipoEntrenamientoDTO>() {
            @Override
            public void onResponse(Call<TipoEntrenamientoDTO> call,
                                   Response<TipoEntrenamientoDTO> response) {
                if (response.isSuccessful()) {
                    loadTypes();
                }
            }

            @Override
            public void onFailure(Call<TipoEntrenamientoDTO> call, Throwable t) {
                // No se actualiza lista si hay error
            }
        });
    }

    /**
     * Actualiza el tipo de entrenamiento identificado por {@code id}, asignándole el nuevo nombre.
     * <p>
     * Construye un DTO con el id y nombre, envía la solicitud y, de ser exitoso,
     * recarga la lista de tipos.
     * </p>
     *
     * @param id     Identificador del tipo a actualizar.
     * @param nombre Nuevo nombre para el tipo.
     */
    public void updateType(int id, String nombre) {
        TipoEntrenamientoDTO dto = new TipoEntrenamientoDTO(id, nombre);
        repository.updateType(id, dto).enqueue(new Callback<TipoEntrenamientoDTO>() {
            @Override
            public void onResponse(Call<TipoEntrenamientoDTO> call,
                                   Response<TipoEntrenamientoDTO> response) {
                if (response.isSuccessful()) {
                    loadTypes();
                }
            }

            @Override
            public void onFailure(Call<TipoEntrenamientoDTO> call, Throwable t) {
                // No se recarga lista si hay error
            }
        });
    }

    /**
     * Elimina el tipo de entrenamiento con el identificador {@code typeId}.
     * <p>
     * Si la eliminación es exitosa, recarga la lista de tipos y notifica éxito
     * mediante {@link DeletionCallback#onSuccess()}. En caso de fallo, invoca
     * {@link DeletionCallback#onFailure(String)} con un mensaje de error.
     * </p>
     *
     * @param typeId   ID del tipo a eliminar.
     * @param callback Objeto que recibe la notificación de éxito o error.
     */
    public void deleteType(int typeId, DeletionCallback callback) {
        repository.deleteType(typeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadTypes(); // Actualizar lista tras eliminar
                    callback.onSuccess();
                } else {
                    String msg = getApplication().getString(R.string.error_delete_type);
                    callback.onFailure(msg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String msg = getApplication().getString(R.string.error_delete_type) + ": " + t.getMessage();
                callback.onFailure(msg);
            }
        });
    }

    /**
     * Interface para recibir callbacks tras intentar eliminar un tipo.
     */
    public interface DeletionCallback {
        /**
         * Se invoca cuando la eliminación se realiza con éxito.
         */
        void onSuccess();

        /**
         * Se invoca cuando ocurre un error al intentar eliminar.
         *
         * @param message Mensaje de error describiendo la causa.
         */
        void onFailure(String message);
    }
}
