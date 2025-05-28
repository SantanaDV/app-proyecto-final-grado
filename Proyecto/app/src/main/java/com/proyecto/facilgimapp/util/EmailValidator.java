package com.proyecto.facilgimapp.util;

import java.util.regex.Pattern;

/**
 * Valida que el email tenga el formato básico texto@texto.dominio
 */
public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
