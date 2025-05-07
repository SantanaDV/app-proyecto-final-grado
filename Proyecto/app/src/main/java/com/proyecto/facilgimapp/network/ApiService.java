package com.proyecto.facilgimapp.network;

import com.proyecto.facilgimapp.model.Ejercicio;
import com.proyecto.facilgimapp.model.dto.EjercicioDTO;
import com.proyecto.facilgimapp.model.dto.EjercicioDeleteDTO;
import com.proyecto.facilgimapp.model.Entrenamiento;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioDTO;
import com.proyecto.facilgimapp.model.dto.UsuarioRequestDTO;
import com.proyecto.facilgimapp.model.LoginRequest;
import com.proyecto.facilgimapp.model.LoginResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    // ===== EJERCICIOS =====
    @GET("api/ejercicios")
    Call<List<Ejercicio>> listAllExercises();

    @GET("api/ejercicios/{id}")
    Call<Ejercicio> getExercise(@Path("id") int id);

    @GET("api/ejercicios/nombre/{nombre}")
    Call<Ejercicio> getExerciseByName(@Path("nombre") String nombre);



    @Multipart
    @POST("api/ejercicios")
    Call<Ejercicio> createOrUpdateExercise(
            @Part("ejercicio") RequestBody ejercicioJson,
            @Part MultipartBody.Part imagen
    );

    @GET("api/ejercicios/entrenamiento/{idEntrenamiento}")
    Call<List<EjercicioDTO>> listExercisesByTraining(
            @Path("idEntrenamiento") int trainingId,
            @Query("username") String username
    );

    // --- Relaciones Entrenamiento–Ejercicio ---
    @GET("api/entrenamiento-ejercicio/entrenamiento/{idEntrenamiento}")
    Call<List<EntrenamientoEjercicioDTO>> listTrainingRelations(
            @Path("idEntrenamiento") int trainingId
    );

    @DELETE("api/ejercicios/{id}")
    Call<Void> deleteExercise(@Path("id") int id);

    @DELETE("api/ejercicios/nombre")
    Call<Void> deleteExerciseByName(@Body EjercicioDeleteDTO dto);

    // ===== ENTRENAMIENTOS =====
    @GET("api/entrenamientos")
    Call<List<Entrenamiento>> listAllTrainings();

    @GET("api/entrenamientos/{id}")
    Call<Entrenamiento> getTraining(@Path("id") int id);

    @GET("api/entrenamientos/fecha")
    Call<List<Entrenamiento>> listTrainingsBetweenDates(
            @Query("fechaInicio") String from,
            @Query("fechaFin")   String to
    );

    @POST("api/entrenamientos")
    Call<Entrenamiento> createTraining(@Body EntrenamientoDTO dto);

    @PUT("api/entrenamientos/{id}")
    Call<Entrenamiento> updateTraining(
            @Path("id") int id,
            @Body EntrenamientoDTO dto
    );

    @DELETE("api/entrenamientos/{id}")
    Call<Void> deleteTraining(@Path("id") int id);

    // Si necesitas estos endpoints extra, añádelos explícitamente:
    // @GET("api/entrenamientos/nombre/{nombre}") …
    // @PUT("api/entrenamientos/nombre/{nombre}") …
    // @PUT("api/entrenamientos/dto/{id}") …

    // ===== RELACIÓN ENTRENAMIENTO–EJERCICIO =====
    @GET("api/entrenamiento-ejercicio")
    Call<List<EntrenamientoEjercicioDTO>> listAllTrainingExercisesRelations();

    @GET("api/entrenamiento-ejercicio/{id}")
    Call<EntrenamientoEjercicioDTO> getTrainingExerciseRelation(@Path("id") int id);

    @POST("api/entrenamiento-ejercicio")
    Call<EntrenamientoEjercicioDTO> addExerciseToTraining(@Body EntrenamientoEjercicioDTO dto);

    @DELETE("api/entrenamiento-ejercicio/{id}")
    Call<Void> removeExerciseFromTraining(@Path("id") int id);

    @GET("api/entrenamiento-ejercicio/entrenamiento/{idEntrenamiento}")
    Call<List<EntrenamientoEjercicioDTO>> listExercisesInTraining(@Path("idEntrenamiento") int idEntrenamiento);

    // ===== SERIES =====
    @GET("api/series/entrenamiento-ejercicio/{id}")
    Call<List<SerieDTO>> listSeries(@Path("id") int relacionId);

    @GET("api/series/{id}")
    Call<SerieDTO> getSeries(@Path("id") int id);

    @POST("api/series")
    Call<SerieDTO> createSeries(@Body SerieDTO dto);

    @PUT("api/series/{id}")
    Call<SerieDTO> updateSeries(
            @Path("id") int id,
            @Body SerieDTO dto
    );

    @DELETE("api/series/{id}")
    Call<Void> deleteSeries(@Path("id") int id);

    // ===== TIPOS DE ENTRENAMIENTO =====
    @GET("api/tipos-entrenamiento")
    Call<List<TipoEntrenamientoDTO>> listTypes();

    @GET("api/tipos-entrenamiento/{id}")
    Call<TipoEntrenamientoDTO> getType(@Path("id") long id);

    @POST("api/tipos-entrenamiento")
    Call<TipoEntrenamientoDTO> createType(@Body TipoEntrenamientoDTO dto);

    @PUT("api/tipos-entrenamiento/{id}")
    Call<TipoEntrenamientoDTO> updateType(
            @Path("id") long id,
            @Body TipoEntrenamientoDTO dto
    );

    @DELETE("api/tipos-entrenamiento/{id}")
    Call<Void> deleteType(@Path("id") long id);

    // ===== USUARIOS =====
    @GET("api/usuarios")
    Call<List<UsuarioDTO>> listUsers();

    @GET("api/usuarios/{id}")
    Call<UsuarioDTO> getUser(@Path("id") int id);

    @GET("api/usuarios/username/{username}")
    Call<UsuarioDTO> getUserByUsername(@Path("username") String username);

    @POST("api/usuarios")
    Call<String> createUser(@Body UsuarioDTO dto);

    @POST("api/usuarios/registrar")
    Call<UsuarioDTO> registerUser(@Body UsuarioRequestDTO dto);

    @PUT("api/usuarios/{id}")
    Call<UsuarioDTO> updateUser(@Path("id") int id, @Body UsuarioDTO dto);

    @PUT("api/usuarios/username/{username}")
    Call<UsuarioDTO> updateUserByUsername(@Path("username") String username, @Body UsuarioDTO dto);

    @DELETE("api/usuarios/{id}")
    Call<String> deleteUser(@Path("id") int id);

    @DELETE("api/usuarios/username/{username}")
    Call<String> deleteUserByUsername(@Path("username") String username);

    // ===== AUTH =====
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
