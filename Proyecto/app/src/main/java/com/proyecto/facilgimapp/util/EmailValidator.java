package com.proyecto.facilgimapp.util;

import java.util.regex.Pattern;

/**
 * Utilidad para validar que una cadena tenga el formato básico de correo electrónico:
 * texto@texto.dominio. Emplea una expresión regular que verifica:
 * <ul>
 *     <li>Parte local: combinaciones de letras, dígitos y algunos caracteres permitidos (._%+-)</li>
 *     <li>Arroba (@) como separador</li>
 *     <li>Dominio: combinaciones de letras, dígitos y guiones, seguido de un punto y al menos dos letras</li>
 * </ul>
 * Ejemplos válidos: usuario@example.com, user.name@mail.co, etc.
 *
 * Autor: Francisco Santana
 */
public class EmailValidator {
    /**
     * Patrón compilado que representa el formato válido de correo electrónico:
     * <code>^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$</code>
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Verifica si la cadena proporcionada cumple con el patrón de un correo válido.
     *
     * @param email Cadena a validar como correo electrónico.
     * @return {@code true} si {@code email} no es null y coincide con {@link #EMAIL_PATTERN};
     *         {@code false} en caso contrario.
     */
    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
