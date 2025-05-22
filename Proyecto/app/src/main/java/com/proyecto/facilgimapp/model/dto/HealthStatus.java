package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

public class HealthStatus {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
