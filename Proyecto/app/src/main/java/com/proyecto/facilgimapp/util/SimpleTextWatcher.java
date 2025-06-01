package com.proyecto.facilgimapp.util;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

/**
 * TextWatcher simplificado que recibe un {@link Consumer<String>} para manejar
 * únicamente el evento {@link #onTextChanged(CharSequence, int, int, int)}.
 * <p>
 * Evita tener que implementar los tres métodos si solo interesa procesar
 * cambios de texto. Los métodos {@code beforeTextChanged} y {@code afterTextChanged}
 * se dejan vacíos.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public class SimpleTextWatcher implements TextWatcher {
    /**
     * Función que se ejecuta cuando cambia el texto. Recibe la cadena actual.
     */
    private final Consumer<String> onTextChanged;

    /**
     * Constructor privado que recibe el consumidor para el evento onTextChanged.
     *
     * @param onTextChanged Función que acepta la cadena resultante tras el cambio de texto.
     */
    private SimpleTextWatcher(Consumer<String> onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    /**
     * Crea una instancia de {@link SimpleTextWatcher} que ejecuta el consumidor
     * pasado cuando el texto cambia.
     *
     * @param consumer Función que recibirá el texto cada vez que cambie.
     * @return Nueva instancia de {@code SimpleTextWatcher}.
     */
    public static SimpleTextWatcher onTextChanged(Consumer<String> consumer) {
        return new SimpleTextWatcher(consumer);
    }

    /**
     * Se invoca antes de que el texto cambie. Implementación vacía (no-op).
     *
     * @param s      Texto actual antes del cambio.
     * @param start  Índice donde comenzará el cambio.
     * @param count  Cantidad de caracteres que se reemplazarán.
     * @param after  Cantidad de caracteres nuevos que se insertarán.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // no-op
    }

    /**
     * Se invoca cuando el texto cambia. Llama al consumidor con la nueva cadena.
     *
     * @param s      Cadena tras el cambio.
     * @param start  Índice donde comenzó el cambio.
     * @param before Cantidad de caracteres reemplazados.
     * @param count  Cantidad de caracteres nuevos insertados.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged.accept(s.toString());
    }

    /**
     * Se invoca después de que el texto ha cambiado. Implementación vacía (no-op).
     *
     * @param s Editable que contiene el texto final tras el cambio.
     */
    @Override
    public void afterTextChanged(Editable s) {
        // no-op
    }
}
