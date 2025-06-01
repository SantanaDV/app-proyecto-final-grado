package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.google.gson.Gson;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EjercicioDeleteDTO;
import com.proyecto.facilgimapp.network.ApiService;
import com.proyecto.facilgimapp.network.RetrofitClient;
import java.io.File;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
/**
 * Repositorio encargado de gestionar las operaciones CRUD de ejercicios
 * contra la API REST mediante Retrofit. Incluye métodos para listar,
 * obtener, crear/actualizar y eliminar ejercicios.
 * 
 * @utor: Francisco Santana
 */
public class EjercicioRepository {
    private final ApiService apiService;
    /**
     * Inicializa el repositorio obteniendo el ApiService de RetrofitClient.
     *
     * @param context Contexto de la aplicación, necesario para configurar Retrofit.
     */
    public EjercicioRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }
    /**
     * Lista todos los ejercicios disponibles.
     *
     * @return Un objeto Call que representa la solicitud para obtener la lista de ejercicios.
     */
    public Call<List<EjercicioDTO>> listAllExercises() {
        return apiService.listAllExercises();
    }
    /**
     * Obtiene un ejercicio específico por su ID.
     *
     * @param id El identificador del ejercicio a obtener.
     * @return Un objeto Call que representa la solicitud para obtener el ejercicio.
     */
    public Call<EjercicioDTO> getExercise(int id) {
        return apiService.getExercise(id);
    }
    /**
     * Obtiene un ejercicio específico por su nombre.
     *
     * @param name El nombre del ejercicio a obtener.
     * @return Un objeto Call que representa la solicitud para obtener el ejercicio.
     */
    public Call<EjercicioDTO> getExerciseByName(String name) {
        return apiService.getExerciseByName(name);
    }
    /**
     * Lista los ejercicios asociados a un entrenamiento específico.
     *
     * @param trainingId El identificador del entrenamiento.
     * @param username El nombre de usuario del propietario del entrenamiento.
     * @return Un objeto Call que representa la solicitud para obtener la lista de ejercicios.
     */
    public Call<List<EjercicioDTO>> listExercisesByTraining(int trainingId, String username) {
        return apiService.listExercisesByTraining(trainingId, username);
    }
    /**
     * Crea o actualiza un ejercicio, enviando sus datos y una imagen opcional.
     *
     * @param ejercicio El objeto EjercicioDTO que contiene los datos del ejercicio.
     * @param imageFile Archivo de imagen asociado al ejercicio, puede ser nulo.
     * @return Un objeto Call que representa la solicitud para crear o actualizar el ejercicio.
     */
    public Call<EjercicioDTO> createOrUpdateExercise(EjercicioDTO ejercicio, File imageFile) {
        Gson gson = new Gson();
        String ejercicioJson = gson.toJson(ejercicio);
        RequestBody ejercicioBody = RequestBody.create(
                MediaType.parse("application/json"), ejercicioJson);
        MultipartBody.Part imagenParte = null;
        if (imageFile != null) {
            RequestBody imagenBody = RequestBody.create(
                    MediaType.parse("image/*"), imageFile);
            imagenParte = MultipartBody.Part.createFormData(
                    "imagen", imageFile.getName(), imagenBody);
        }
        return apiService.createOrUpdateExercise(ejercicioBody, imagenParte);
    }
    /**
     * Elimina un ejercicio por su ID.
     *
     * @param id El identificador del ejercicio a eliminar.
     * @return Un objeto Call que representa la solicitud para eliminar el ejercicio.
     */
    public Call<Void> deleteExercise(int id) {
        return apiService.deleteExercise(id);
    }
    /**
     * Elimina un ejercicio por su nombre.
     *
     * @param nombre El nombre del ejercicio a eliminar.
     * @return Un objeto Call que representa la solicitud para eliminar el ejercicio.
     */
    public Call<Void> deleteExerciseByName(String nombre) {
        EjercicioDeleteDTO dto = new EjercicioDeleteDTO();
        dto.setNombre(nombre);
        return apiService.deleteExerciseByName(dto);
    }
}
