package com.proyecto.facilgimapp.util;

import java.util.regex.Pattern;

/**
 * Utilidad para validar el formato de contraseñas según los siguientes criterios:
 * <ul>
 *     <li>Longitud entre 8 y 15 caracteres.</li>
 *     <li>Al menos una letra mayúscula.</li>
 *     <li>Al menos una letra minúscula.</li>
 *     <li>Al menos un dígito numérico.</li>
 *     <li>Al menos un carácter especial (no alfanumérico).</li>
 *     <li>No se permiten espacios en blanco.</li>
 * </ul>
 * 
 * Autor: Francisco Santana
 */
public class PasswordValidator {
    /**
     * Patrón regex que verifica:
     * <ul>
     *   <li> (?=.*[0-9] )  => al menos un dígito.</li>
     *   <li> (?=.*[a-z] )  => al menos una minúscula.</li>
     *   <li> (?=.*[A-Z] )  => al menos una mayúscula.</li>
     *   <li> (?=.*[^a-zA-Z0-9] ) => al menos un carácter especial.</li>
     *   <li> (?=\S+$)      => sin espacios en blanco.</li>
     *   <li> .{8,15}$      => longitud mínima de 8 y máxima de 15 caracteres.</li>
     * </ul>
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])" +         // al menos un dígito
            "(?=.*[a-z])" +          // al menos una minúscula
            "(?=.*[A-Z])" +          // al menos una mayúscula
            "(?=.*[^a-zA-Z0-9])" +   // al menos un carácter especial
            "(?=\\S+$)" +            // sin espacios en blanco
            ".{8,15}$"               // entre 8 y 15 caracteres
    );

    /**
     * Verifica si la contraseña proporcionada cumple con el patrón definido.
     *
     * @param pwd Cadena de texto que representa la contraseña a validar.
     * @return {@code true} si {@code pwd} no es null y coincide con {@link #PASSWORD_PATTERN};
     *         {@code false} en caso contrario.
     */
    public static boolean isValid(String pwd) {
        return pwd != null && PASSWORD_PATTERN.matcher(pwd).matches();
    }
}
