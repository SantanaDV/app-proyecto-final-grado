package com.proyecto.facilgimapp.request;

import java.util.Objects;



/**
 * Clase que representa la solicitud de inicio de sesión.
 * Esta clase se utiliza para enviar los datos de inicio de sesión al servidor.
 */
public class LoginRequest {

    private String email;
    private String contrasena ;

    public LoginRequest() {
    }

    public LoginRequest(String email, String contrasena ) {
        this.email = email;
        this.contrasena  = contrasena ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(contrasena , that.contrasena );
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, contrasena );
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='" + contrasena  + '\'' +
                '}';
    }
}
