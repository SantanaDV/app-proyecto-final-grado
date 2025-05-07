package com.proyecto.facilgimapp.util;

import android.widget.SeekBar;

public abstract class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // no-op
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // no-op
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // to be overridden
    }
}
