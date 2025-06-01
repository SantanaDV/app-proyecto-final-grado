package com.proyecto.facilgimapp.model.entity;

/**
 * Modelo de datos para cada opción del perfil de usuario.
 * 
 *  @author Francisco Santana
 */
public class UserOptionItem {
    private final UserOptionType type;

    public UserOptionItem(UserOptionType type) {
        this.type = type;
    }

    public UserOptionType getType() {
        return type;
    }
}