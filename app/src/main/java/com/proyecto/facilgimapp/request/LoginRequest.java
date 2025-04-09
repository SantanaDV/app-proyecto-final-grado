package com.proyecto.facilgimapp.request;

import java.util.Objects;



/**
 * Clase que representa la solicitud de inicio de sesión.
 * Esta clase se utiliza para enviar los datos de inicio de sesión al servidor.
 */
public class LoginRequest {

    private String username;
    private String contrasena ;

    public LoginRequest() {
    }

    public LoginRequest(String username, String contrasena ) {
        this.username = username;
        this.contrasena  = contrasena ;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(contrasena , that.contrasena );
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, contrasena );
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + username + '\'' +
                ", password='" + contrasena  + '\'' +
                '}';
    }
}
