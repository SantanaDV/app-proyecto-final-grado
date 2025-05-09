package com.proyecto.facilgimapp.repository;

import android.content.Context;
import com.google.gson.Gson;
import com.proyecto.facilgimapp.model.Ejercicio;
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

public class EjercicioRepository {
    private final ApiService apiService;

    public EjercicioRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public Call<List<EjercicioDTO>> listAllExercises() {
        return apiService.listAllExercises();
    }

    public Call<EjercicioDTO> getExercise(int id) {
        return apiService.getExercise(id);
    }

    public Call<EjercicioDTO> getExerciseByName(String name) {
        return apiService.getExerciseByName(name);
    }

    public Call<List<EjercicioDTO>> listExercisesByTraining(int trainingId, String username) {
        return apiService.listExercisesByTraining(trainingId, username);
    }

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

    public Call<Void> deleteExercise(int id) {
        return apiService.deleteExercise(id);
    }

    public Call<Void> deleteExerciseByName(String nombre) {
        EjercicioDeleteDTO dto = new EjercicioDeleteDTO();
        dto.setNombre(nombre);
        return apiService.deleteExerciseByName(dto);
    }
}
