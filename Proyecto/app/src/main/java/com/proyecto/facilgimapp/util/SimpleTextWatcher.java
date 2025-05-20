package com.proyecto.facilgimapp.util;


import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class SimpleTextWatcher implements TextWatcher {
    private final Consumer<String> onTextChanged;

    private SimpleTextWatcher(Consumer<String> onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    public static SimpleTextWatcher onTextChanged(Consumer<String> consumer) {
        return new SimpleTextWatcher(consumer);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* no-op */ }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged.accept(s.toString());
    }

    @Override public void afterTextChanged(Editable s) { /* no-op */ }
}