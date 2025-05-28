package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import android.util.Log;

/** Helpers para resolver atributos de tema */
public class ThemeUtils {
    private static final String TAG = "THEME";

    /** Resuelve un color desde un attr de tema */
    public static @ColorInt int resolveColor(Context ctx, int attr) {
        TypedArray ta = ctx.getTheme().obtainStyledAttributes(new int[]{ attr });
        int c = ta.getColor(0, Color.MAGENTA);
        ta.recycle();
        Log.d(TAG, String.format(
                "resolveColor: attr=0x%08X → color=#%06X",
                attr, (0xFFFFFF & c)
        ));
        return c;
    }
}
