/**
 * Representa el estado de salud de un usuario.
 * Esta clase se utiliza para mapear la respuesta JSON relacionada con el estado de salud,
 * usando la anotación {@link com.google.gson.annotations.SerializedName} para la deserialización.
 *
 * @author Francisco Santana
 */
package com.proyecto.facilgimapp.model.dto;

import com.google.gson.annotations.SerializedName;

public class HealthStatus {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
