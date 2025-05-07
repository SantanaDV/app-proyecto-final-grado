package com.proyecto.facilgimapp.model;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
