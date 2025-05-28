package com.proyecto.facilgimapp.util;

import java.util.regex.Pattern;

/**
 * Valida que la contraseña cumpla:
 * - 8 a 15 caracteres
 * - al menos una mayúscula, una minúscula, un dígito, un especial
 * - sin espacios
 */
public class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])" +         // al menos un dígito
                    "(?=.*[a-z])" +          // al menos una minúscula
                    "(?=.*[A-Z])" +          // al menos una mayúscula
                    "(?=.*[^a-zA-Z0-9])" +   // al menos un especial
                    "(?=\\S+$)" +            // sin espacios
                    ".{8,15}$"               // entre 8 y 15 chars
    );

    public static boolean isValid(String pwd) {
        return pwd != null && PASSWORD_PATTERN.matcher(pwd).matches();
    }
}
