package com.proyecto.facilgimapp.util;

import android.os.Build;
import android.view.Window;
import android.view.WindowInsets;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;

public class BarUtils {

    /**
     * Cambia el color de la barra de estado:
     * - En API>=35 dibuja un fondo con WindowInsets
     * - En versiones anteriores usa window.statusBarColor
     */
    public static void setStatusBarColor(Window window, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            View decor = window.getDecorView();
            decor.setOnApplyWindowInsetsListener((v, insets) -> {
                // Aquí usamos android.graphics.Insets que devuelve WindowInsets.getInsets(...)
                android.graphics.Insets sb = insets.getInsets(WindowInsets.Type.statusBars());
                v.setBackgroundColor(color);
                v.setPadding(0, sb.top, 0, 0);
                return insets;
            });
            // Fuerza aplicación inicial de insets
            ViewCompat.requestApplyInsets(decor);
        } else {
            // Comportamiento legacy para API < 35
            window.setStatusBarColor(color);
        }
    }
}
