package com.proyecto.facilgimapp.response;


import java.util.Objects;

/**
 * Clase que representa la respuesta de inicio de sesión.
 * Esta clase se utiliza para recibir los datos de inicio de sesión del servidor.
 */

public class LoginResponse {

    private String token;
    //Mensaje de error o de otro tipo de respuesta
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(token, that.token) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, message);
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
