package com.proyecto.facilgimapp.ui.workout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.model.dto.EntrenamientoEjercicioDTO;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clase encargada de mostrar un diálogo para crear o editar un entrenamiento.
 * <p>
 * El diálogo incluye campos para nombre, descripción, duración, fecha y tipo de entrenamiento,
 * así como un listado de ejercicios con sus series correspondientes. Permite seleccionar fecha
 * mediante un DatePicker y actualizar los datos en un {@link EntrenamientoDTO} existente.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class EditWorkoutDialog {

    /**
     * Muestra un diálogo de edición/creación de entrenamiento.
     * <p>
     * Rellena los campos con los datos del {@link EntrenamientoDTO} pasado,
     * permite modificar nombre, descripción, duración, fecha (con DatePicker), tipo de entrenamiento
     * y editar las relaciones ejercicio-serie. Al pulsar "Guardar", valida que:
     * <ul>
     *     <li>El nombre no esté vacío.</li>
     *     <li>La fecha esté seleccionada.</li>
     *     <li>El tipo de entrenamiento esté elegido.</li>
     *     <li>Cada ejercicio tenga al menos una serie.</li>
     *     <li>Cada serie tenga repeticiones > 0, peso > 0 y el checkbox 'completada' marcado.</li>
     * </ul>
     * Si todas las validaciones pasan, actualiza el {@code workout} y llama a {@code onSave.accept(workout)}.
     * </p>
     *
     * @param parent  Fragment que invoca el diálogo; se utiliza para inflar vistas y acceder al contexto.
     * @param workout Objeto {@link EntrenamientoDTO} que se va a editar (sus campos se rellenan inicialmente).
     * @param tipos   Lista de {@link TipoEntrenamientoDTO} disponible para asignar el tipo de entrenamiento.
     * @param onSave  Función que se ejecuta cuando el usuario confirma la edición; recibe el {@code workout} modificado.
     */
    public static void show(Fragment parent,
                            EntrenamientoDTO workout,
                            List<TipoEntrenamientoDTO> tipos,
                            Consumer<EntrenamientoDTO> onSave) {

        // Infla la vista personalizada del diálogo
        View root = LayoutInflater.from(parent.requireContext())
                .inflate(R.layout.dialog_edit_workout_extended, null);

        // Referencias a los campos del diálogo
        EditText etNombre      = root.findViewById(R.id.etNombre);
        EditText etDescripcion = root.findViewById(R.id.etDescripcion);
        EditText etDuracion    = root.findViewById(R.id.etDuracion);
        EditText etFecha       = root.findViewById(R.id.etFecha);

        Spinner spinnerTipo       = root.findViewById(R.id.spinnerTipo);
        LinearLayout llExercises  = root.findViewById(R.id.llExercisesContainer);

        // Rellena los campos con los datos actuales del entrenamiento
        etNombre.setText(workout.getNombre());
        etDescripcion.setText(workout.getDescripcion());
        etDuracion.setText(String.valueOf(workout.getDuracion()));
        etFecha.setText(workout.getFechaEntrenamiento().toString());

        // Configura el DatePicker para el campo de fecha
        etFecha.setFocusable(false);
        etFecha.setClickable(true);
        etFecha.setOnClickListener(v -> {
            LocalDate cur = workout.getFechaEntrenamiento();
            DatePickerDialog dp = new DatePickerDialog(
                    parent.requireContext(),
                    (DatePicker view, int y, int m, int d) -> {
                        LocalDate sel = LocalDate.of(y, m + 1, d);
                        etFecha.setText(sel.toString());
                        workout.setFechaEntrenamiento(sel);
                    },
                    cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth()
            );
            dp.show();
        });

        // Configura el Spinner de tipos de entrenamiento
        List<String> nombres = new ArrayList<>();
        for (TipoEntrenamientoDTO t : tipos) {
            nombres.add(t.getNombre());
        }
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                parent.requireContext(),
                android.R.layout.simple_spinner_item,
                nombres
        );
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spAdapter);
        // Selecciona el tipo actual en el Spinner
        for (int i = 0; i < tipos.size(); i++) {
            if (workout.getTipoEntrenamiento() != null &&
                    tipos.get(i).getId() == (workout.getTipoEntrenamiento().getId())) {
                spinnerTipo.setSelection(i);
                break;
            }
        }

        // Infla cada ejercicio y sus series dentro del LinearLayout llExercises
        LayoutInflater inf = LayoutInflater.from(parent.requireContext());
        for (EntrenamientoEjercicioDTO rel : workout.getEntrenamientosEjercicios()) {
            View ev = inf.inflate(R.layout.item_edit_ejercicio, llExercises, false);
            // Asigna el nombre del ejercicio
            TextView tvName = ev.findViewById(R.id.tvExerciseName);
            tvName.setText(rel.getEjercicio().getNombre());
            // Configura el editor de series para las series existentes
            LinearLayout llSeries = ev.findViewById(R.id.llSeriesContainer);
            SeriesEditorHelper.bindSeriesEditor(llSeries, rel.getSeries());
            llExercises.addView(ev);
        }

        // Construye y muestra el diálogo con botones "Guardar" y "Cancelar"
        AlertDialog dialog = new AlertDialog.Builder(parent.requireContext())
                .setTitle(R.string.editar_entrenamiento)
                .setView(root)
                .setPositiveButton(R.string.action_save, null)   // Listener customizado más abajo
                .setNegativeButton(R.string.action_cancel, null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                // 1) Validar nombre
                String nombreInput = etNombre.getText().toString().trim();
                if (nombreInput.isEmpty()) {
                    etNombre.setError(parent.getString(R.string.error_name_required));
                    etNombre.requestFocus();
                    return;
                }

                // 2) Validar fecha
                String fechaStr = etFecha.getText().toString().trim();
                if (fechaStr.isEmpty()) {
                    Toast.makeText(parent.requireContext(),
                            R.string.error_date_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Ya quedó guardada en workout.setFechaEntrenamiento(sel) en el DatePicker

                // 3) Validar tipo de entrenamiento
                int tipoPos = spinnerTipo.getSelectedItemPosition();
                if (tipoPos < 0 || tipoPos >= tipos.size()) {
                    Toast.makeText(parent.requireContext(),
                            R.string.error_type_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                TipoEntrenamientoDTO tipoElegido = tipos.get(tipoPos);

                // 4) Validar ejercicios y series
                List<EntrenamientoEjercicioDTO> relaciones = workout.getEntrenamientosEjercicios();
                if (relaciones == null || relaciones.isEmpty()) {
                    Toast.makeText(parent.requireContext(),
                            R.string.error_exercises_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (EntrenamientoEjercicioDTO rel : relaciones) {
                    List<SerieDTO> series = rel.getSeries();
                    if (series == null || series.isEmpty()) {
                        Toast.makeText(parent.requireContext(),
                                parent.getString(R.string.debe_quedar_una_serie)
                                        + " (“" + rel.getEjercicio().getNombre() + "”)",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (SerieDTO s : series) {
                        Integer repsObj = s.getRepeticiones();
                        Double pesoObj  = s.getPeso();
                        boolean completada = s.isCompletada();
                        int reps = (repsObj == null ? 0 : repsObj);
                        double peso = (pesoObj == null ? 0.0 : pesoObj);

                        if (!completada || reps <= 0 || peso <= 0) {
                            Toast.makeText(parent.requireContext(),
                                    parent.getString(R.string.debe_completar_todas_las_series)
                                            + " (“" + rel.getEjercicio().getNombre() + "”)",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                // 5) Si todas las validaciones pasan, actualizar el DTO
                workout.setNombre(nombreInput);
                workout.setDescripcion(etDescripcion.getText().toString().trim());
                try {
                    workout.setDuracion(Integer.parseInt(etDuracion.getText().toString().trim()));
                } catch (NumberFormatException nf) {
                    Toast.makeText(parent.requireContext(),
                            R.string.error_required_fields, Toast.LENGTH_SHORT).show();
                    return;
                }
                workout.setTipoEntrenamiento(tipoElegido);

                // Limpiar IDs de relaciones para forzar inserción/actualización limpia
                for (EntrenamientoEjercicioDTO rel : relaciones) {
                    rel.setId(null);
                    for (SerieDTO s : rel.getSeries()) {
                        s.setId(null);
                    }
                }

                // Llama al callback para guardar los cambios en el backend
                onSave.accept(workout);
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}
