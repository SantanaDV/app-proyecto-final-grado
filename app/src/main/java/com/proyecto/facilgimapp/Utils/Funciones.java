package com.proyecto.facilgimapp.Utils;

/**
 * Clase de utilidades con distitns funciones utiles a lo largo del programa
 */
public class Funciones {

    /**
     * Comprueba que el string solo contiene numeros y letras
     * @param contrasena
     * @return boolean
     */
    private boolean stringNumerosYLetras(String contrasena) {
        String regex = "^[a-zA-Z0-9]+$";
        return contrasena.matches(regex);
    }

    /**
     * Comprueba que el string contiene mas de 6 caracteres
     * @param contrasena
     * @return boolean
     */
    private boolean stringMayorASeisCaracteres(String contrasena) {
        return contrasena.length() >= 6;
    }
}
