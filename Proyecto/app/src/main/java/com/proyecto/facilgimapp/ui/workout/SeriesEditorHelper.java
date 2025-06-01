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

import java.util.List;
import java.util.function.Consumer;

/**
 * Clase auxiliar que gestiona la edición dinámica de series dentro de un contenedor {@link LinearLayout}.
 * <p>
 * Permite cargar una lista de objetos {@link SerieDTO} en vistas individuales, incluyendo campos
 * para repeticiones, peso y un checkbox de completado, así como un botón para agregar nuevas series.
 * Además, ofrece funcionalidad para eliminar series con un gesto de pulsación prolongada.
 * </p>
 * 
 * @author Francisco Santana
 */
public class SeriesEditorHelper {

    /**
     * Enlaza la lista de {@link SerieDTO} al contenedor proporcionado, precargando cada serie existente
     * y añadiendo la funcionalidad para añadir o eliminar series.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     *   <li>Limpia el contenedor eliminando todas las vistas previas.</li>
     *   <li>Infla y agrega una vista por cada objeto {@code SerieDTO} en la lista, usando {@link #addOneSeriesView}.</li>
     *   <li>Agrega un botón al final del contenedor que permite crear una nueva {@code SerieDTO} con valores por defecto
     *   (repeticiones = 0, peso = 0.0) y mostrar su vista correspondiente antes del botón.</li>
     * </ol>
     * </p>
     *
     * @param container Contenedor {@link LinearLayout} en el que se mostrarán las vistas de series.
     * @param series    Lista de objetos {@link SerieDTO} que representan las series existentes.
     *                  Las modificaciones en la interfaz (añadir/eliminar) se reflejarán en esta lista.
     */
    public static void bindSeriesEditor(
            LinearLayout container,
            List<SerieDTO> series
    ) {
        // Limpiamos el contenedor de vistas anteriores
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        // Inflamos y añadimos una vista para cada serie existente
        for (int i = 0; i < series.size(); i++) {
            addOneSeriesView(container, series.get(i), series::remove, inflater, i);
        }

        // Creamos e inflamos el botón "Añadir serie" que permite crear nuevas series
        View btnAdd = inflater.inflate(R.layout.item_add_serie_button, container, false);
        btnAdd.setOnClickListener(v -> {
            // Crear un nuevo DTO de serie con valores por defecto
            SerieDTO nueva = new SerieDTO();
            nueva.setRepeticiones(0);
            nueva.setPeso(0.0);
            // Añadir el DTO al modelo
            series.add(nueva);
            // Insertar la vista de la nueva serie justo antes del botón
            addOneSeriesView(container, nueva, series::remove, inflater, container.getChildCount() - 1);
        });
        container.addView(btnAdd);
    }

    /**
     * Infla y configura la vista correspondiente a una única {@link SerieDTO}, inserta dicha vista
     * en la posición indicada dentro del contenedor, y gestiona sus listeners.
     * <p>
     * Cada vista de serie contiene:
     * <ul>
     *   <li>Un campo {@link EditText} para repeticiones.</li>
     *   <li>Un campo {@link EditText} para peso.</li>
     *   <li>Un {@link CheckBox} que marca la serie como completada.</li>
     * </ul>
     * <p>
     * Al crear la vista, se precargan los valores de repeticiones y peso del DTO. Si ambos valores
     * son mayores que cero, se marca automáticamente el checkbox. Además:
     * <ul>
     *   <li>Se añade un {@link TextWatcher} que actualiza el DTO al cambiar los campos de texto
     *       y marca automáticamente el checkbox cuando ambas entradas son positivas.</li>
     *   <li>Se añade un listener al checkbox para actualizar el campo {@code completada} en el DTO.</li>
     *   <li>Se añade un listener de pulsación prolongada sobre la vista completa para eliminarla del contenedor
     *       y notificar al consumidor {@code onRemove} para que elimine el DTO de la lista.</li>
     * </ul>
     *
     * @param container Contenedor {@link LinearLayout} donde se insertará la vista de la serie.
     * @param serie     Objeto {@link SerieDTO} que contiene datos de repeticiones, peso y estado.
     * @param onRemove  Función {@link Consumer} a la que se le pasará el DTO para eliminarlo de la lista
     *                  cuando se solicite su eliminación.
     * @param inflater  {@link LayoutInflater} para inflar el layout de la vista de serie (R.layout.item_serie).
     * @param index     Índice donde se insertará la nueva vista dentro del contenedor. Si es -1 o mayor que
     *                  el número de hijos, la vista se añadirá al final.
     */
    private static void addOneSeriesView(
            LinearLayout container,
            SerieDTO serie,
            Consumer<SerieDTO> onRemove,
            LayoutInflater inflater,
            int index
    ) {
        // Inflar el layout de una serie individual
        View view = inflater.inflate(R.layout.item_serie, container, false);
        EditText etReps   = view.findViewById(R.id.etRepsSerie);
        EditText etWeight = view.findViewById(R.id.etWeightSerie);
        CheckBox cbDone   = view.findViewById(R.id.cbDoneSerie);

        // Precargar los valores de repeticiones y peso en los EditText
        etReps.setText(String.valueOf(serie.getRepeticiones()));
        etWeight.setText(String.valueOf(serie.getPeso()));

        // Marcar el checkbox si la serie ya tenía repeticiones y peso mayores que cero
        boolean hasReps   = serie.getRepeticiones() > 0;
        boolean hasWeight = serie.getPeso() > 0;
        if (hasReps && hasWeight) {
            cbDone.setChecked(true);
            serie.setCompletada(true);
        }

        // Crear un TextWatcher que actualiza el DTO al modificar los campos de texto
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Actualizar repeticiones en el DTO
                int reps = 0;
                try { reps = Integer.parseInt(etReps.getText().toString()); } catch(Exception ignored){}
                serie.setRepeticiones(reps);

                // Actualizar peso en el DTO
                double peso = 0;
                try { peso = Double.parseDouble(etWeight.getText().toString()); } catch(Exception ignored){}
                serie.setPeso(peso);

                // Si ambos valores son mayores que cero y el checkbox no estaba marcado, marcarlo
                if (reps > 0 && peso > 0 && !cbDone.isChecked()) {
                    cbDone.setChecked(true);
                }
            }
        };
        etReps.addTextChangedListener(watcher);
        etWeight.addTextChangedListener(watcher);

        // Listener para el checkbox que actualiza el campo 'completada' en el DTO
        cbDone.setOnCheckedChangeListener((btn, checked) ->
                serie.setCompletada(checked)
        );

        // Listener de pulsación prolongada para eliminar la vista y el DTO
        view.setOnLongClickListener(v -> {
            container.removeView(view);
            onRemove.accept(serie);
            return true;
        });

        // Insertar la vista en la posición indicada
        if (index >= 0 && index < container.getChildCount()) {
            container.addView(view, index);
        } else {
            container.addView(view);
        }
    }
}
