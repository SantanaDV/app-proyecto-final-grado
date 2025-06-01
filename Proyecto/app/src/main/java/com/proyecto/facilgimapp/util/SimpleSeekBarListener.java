package com.proyecto.facilgimapp.util;

import android.widget.SeekBar;

/**
 * Adaptador simplificado de {@link SeekBar.OnSeekBarChangeListener} que evita
 * tener que implementar métodos que no se usan. Proporciona implementaciones
 * vacías para {@link #onProgressChanged} y {@link #onStartTrackingTouch}, y deja
 * {@link #onStopTrackingTouch} para que sea sobreescrito si se necesita.
 * <p>
 * Ideal cuando solo interesa detectar el evento de fin de arrastre del SeekBar.
 * </p>
 * 
 * Autor: Francisco Santana
 */
public abstract class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
    /**
     * Se llama cuando cambia el progreso del SeekBar. Implementación vacía
     * para evitar tener que implementarlo si no se necesita.
     *
     * @param seekBar  Vista SeekBar cuyo progreso cambió.
     * @param progress Nuevo valor de progreso.
     * @param fromUser {@code true} si el cambio proviene de una interacción del usuario.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // no-op
    }

    /**
     * Se llama cuando el usuario comienza a arrastrar el SeekBar. Implementación vacía
     * para evitar tener que implementarlo si no se necesita.
     *
     * @param seekBar Vista SeekBar que el usuario está tocando.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // no-op
    }

    /**
     * Se llama cuando el usuario deja de arrastrar el SeekBar. Este método debe
     * ser sobreescrito si se desea manejar el evento de fin de arrastre.
     *
     * @param seekBar Vista SeekBar de la cual se detuvo el arrastre.
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // to be overridden
    }
}
