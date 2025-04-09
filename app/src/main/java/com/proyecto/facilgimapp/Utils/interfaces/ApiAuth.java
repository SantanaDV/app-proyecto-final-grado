package com.proyecto.facilgimapp.Utils.interfaces;



import com.proyecto.facilgimapp.request.LoginRequest;
import com.proyecto.facilgimapp.response.LoginResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiAuth {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);



}