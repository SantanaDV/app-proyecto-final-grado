package com.proyecto.facilgimapp.ui.workout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.util.SimpleTextWatcher;

import java.util.List;
import java.util.function.Consumer;


public class SeriesEditorHelper {

    /**
     * Enlaza la lista de SerieDTO al container, precargando checkbox
     * marcado si la serie ya tenía datos y manteniendo
     * la funcionalidad de añadir/eliminar.
     */
    public static void bindSeriesEditor(
            LinearLayout container,
            List<SerieDTO> series
    ) {
        // Limpiamos el contenedor
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        // Dibujamos cada serie existente
        for (int i = 0; i < series.size(); i++) {
            addOneSeriesView(container, series.get(i), series::remove, inflater, i);
        }

        // Botón  Añadir serie
        View btnAdd = inflater.inflate(R.layout.item_add_serie_button, container, false);
        btnAdd.setOnClickListener(v -> {
            // Creamos el  DTO con valores por defecto
            SerieDTO nueva = new SerieDTO();
            nueva.setRepeticiones(0);
            nueva.setPeso(0.0);
            // Añadimos al modelo
            series.add(nueva);
            // Insertamos una  vista antes del botón
            addOneSeriesView(container, nueva, series::remove, inflater, container.getChildCount() - 1);
        });
        container.addView(btnAdd);
    }

    /**
     * Infla una sola vista de serie, la añade en la posición indicada,
     * y gestiona sus listeners y el checkbox inicial.
     */
    private static void addOneSeriesView(
            LinearLayout container,
            SerieDTO serie,
            Consumer<SerieDTO> onRemove,
            LayoutInflater inflater,
            int index
    ) {
        // Inflar el  layout
        View view = inflater.inflate(R.layout.item_serie, container, false);
        EditText etReps   = view.findViewById(R.id.etRepsSerie);
        EditText etWeight = view.findViewById(R.id.etWeightSerie);
        CheckBox cbDone   = view.findViewById(R.id.cbDoneSerie);

        // Precargamos los valores
        etReps.setText(String.valueOf(serie.getRepeticiones()));
        etWeight.setText(String.valueOf(serie.getPeso()));

        // Marcamos los checkbox si ya tenía datos
        boolean hasReps   = serie.getRepeticiones() > 0;
        boolean hasWeight = serie.getPeso() > 0;
        if (hasReps && hasWeight) {
            cbDone.setChecked(true);
            serie.setCompletada(true);
        }

        // TextWatcher común para reps y peso
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Actualiza DTO
                int reps = 0;
                try { reps = Integer.parseInt(etReps.getText().toString()); } catch(Exception ignored){}
                serie.setRepeticiones(reps);

                double peso = 0;
                try { peso = Double.parseDouble(etWeight.getText().toString()); } catch(Exception ignored){}
                serie.setPeso(peso);

                // Auto-marca checkbox
                if (reps > 0 && peso > 0 && !cbDone.isChecked()) {
                    cbDone.setChecked(true);
                }
            }
        };
        etReps.addTextChangedListener(watcher);
        etWeight.addTextChangedListener(watcher);

        // Listener checkbox para completado
        cbDone.setOnCheckedChangeListener((btn, checked) ->
                serie.setCompletada(checked)
        );

        // Eliminar serie con long-click
        view.setOnLongClickListener(v -> {
            container.removeView(view);
            onRemove.accept(serie);
            return true;
        });

        // Añadir al container
        container.addView(view, index);
    }
}

