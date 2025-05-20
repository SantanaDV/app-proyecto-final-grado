package com.proyecto.facilgimapp.ui.workout;

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
     * Pinta las vistas de todas las series y deja un botón al final
     * para añadir más series justo **antes** de ese botón.
     */
    public static void bindSeriesEditor(
            LinearLayout container,
            List<SerieDTO> series
    ) {
        // 1) Limpiar
        container.removeAllViews();

        // 2) Dibujar cada serie existente
        for (SerieDTO s : series) {
            addOneSeriesView(container, s, series::remove);
        }

        // 3) Botón “+ Añadir serie”
        View btnAdd = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_add_serie_button, container, false);
        btnAdd.setOnClickListener(v -> {
            // Crear DTO con valores por defecto
            SerieDTO nueva = new SerieDTO();
            nueva.setRepeticiones(0);
            nueva.setPeso(0.0);
            series.add(nueva);

            // Insertar la nueva vista **antes** del botón (último hijo)
            int insertIndex = container.getChildCount() - 1;
            addOneSeriesView(container, nueva, series::remove, insertIndex);
        });
        container.addView(btnAdd);
    }

    // Sobrecarga para mantener compatibilidad: añade al final
    private static void addOneSeriesView(
            LinearLayout container,
            SerieDTO serie,
            Consumer<SerieDTO> onRemove
    ) {
        addOneSeriesView(container, serie, onRemove, container.getChildCount());
    }

    // Nuevo método que admite índice de inserción
    private static void addOneSeriesView(
            LinearLayout container,
            SerieDTO serie,
            Consumer<SerieDTO> onRemove,
            int index
    ) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_serie, container, false);

        EditText etReps   = view.findViewById(R.id.etRepsSerie);
        EditText etWeight = view.findViewById(R.id.etWeightSerie);
        CheckBox cbDone   = view.findViewById(R.id.cbDoneSerie);

        // Inicializar valores en los campos
        etReps.setText(String.valueOf(serie.getRepeticiones()));
        etWeight.setText(String.valueOf(serie.getPeso()));
        cbDone.setChecked(serie.isCompletada());

        // TextWatchers para actualizar DTO
        etReps.addTextChangedListener(
                SimpleTextWatcher.onTextChanged(s -> {
                    try { serie.setRepeticiones(Integer.parseInt(s)); }
                    catch (NumberFormatException ignored) {}
                })
        );
        etWeight.addTextChangedListener(
                SimpleTextWatcher.onTextChanged(s -> {
                    try { serie.setPeso(Double.parseDouble(s)); }
                    catch (NumberFormatException ignored) {}
                })
        );
        cbDone.setOnCheckedChangeListener((b, ic) -> serie.setCompletada(ic));

        // Long click para eliminar
        view.setOnLongClickListener(v -> {
            container.removeView(view);
            onRemove.accept(serie);
            return true;
        });

        // Insertar en la posición deseada
        container.addView(view, index);
    }
}
