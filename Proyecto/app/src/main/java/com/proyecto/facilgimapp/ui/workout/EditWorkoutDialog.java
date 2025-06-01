package com.proyecto.facilgimapp.ui.workout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class EditWorkoutDialog {

    public static void show(Fragment parent,
                            EntrenamientoDTO workout,
                            List<TipoEntrenamientoDTO> tipos,
                            Consumer<EntrenamientoDTO> onSave) {

        View root = LayoutInflater.from(parent.requireContext())
                .inflate(R.layout.dialog_edit_workout_extended, null);

        //  Referencias
        EditText etNombre      = root.findViewById(R.id.etNombre);
        EditText etDescripcion = root.findViewById(R.id.etDescripcion);
        EditText etDuracion    = root.findViewById(R.id.etDuracion);
        EditText etFecha       = root.findViewById(R.id.etFecha);

        Spinner spinnerTipo    = root.findViewById(R.id.spinnerTipo);
        LinearLayout llExercises = root.findViewById(R.id.llExercisesContainer);

        //  Rellenar datos
        etNombre.setText(workout.getNombre());
        etDescripcion.setText(workout.getDescripcion());
        etDuracion.setText(String.valueOf(workout.getDuracion()));
        etFecha.setText(workout.getFechaEntrenamiento().toString());

        //  DatePicker
        etFecha.setFocusable(false);
        etFecha.setClickable(true);
        etFecha.setOnClickListener(v -> {
            LocalDate cur = workout.getFechaEntrenamiento();
            DatePickerDialog dp = new DatePickerDialog(
                    parent.requireContext(),
                    (DatePicker view, int y, int m, int d) -> {
                        LocalDate sel = LocalDate.of(y, m+1, d);
                        etFecha.setText(sel.toString());
                        workout.setFechaEntrenamiento(sel);
                    },
                    cur.getYear(), cur.getMonthValue() - 1, cur.getDayOfMonth()
            );
            dp.show();
        });

        //  Spinner de tipos
        List<String> nombres = new ArrayList<>();
        for (TipoEntrenamientoDTO t : tipos) nombres.add(t.getNombre());
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                parent.requireContext(),
                android.R.layout.simple_spinner_item,
                nombres
        );
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(spAdapter);
        // Selección actual
        for (int i = 0; i < tipos.size(); i++) {
            if (tipos.get(i).getId() == (workout.getTipoEntrenamiento().getId())) {
                spinnerTipo.setSelection(i);
                break;
            }
        }

        // Inflar cada ejercicio + sus series
        LayoutInflater inf = LayoutInflater.from(parent.requireContext());
        for (EntrenamientoEjercicioDTO rel : workout.getEntrenamientosEjercicios()) {
            View ev = inf.inflate(R.layout.item_edit_ejercicio, llExercises, false);
            // Nombre ejercicio
            TextView tvName = ev.findViewById(R.id.tvExerciseName);
            tvName.setText(rel.getEjercicio().getNombre());
            // Series editor helper
            LinearLayout llSeries = ev.findViewById(R.id.llSeriesContainer);
            SeriesEditorHelper.bindSeriesEditor(llSeries, rel.getSeries());
            llExercises.addView(ev);
        }

        //  Botones del diálogo
        new AlertDialog.Builder(parent.requireContext())
                .setTitle(R.string.editar_entrenamiento)
                .setView(root)
                .setPositiveButton(R.string.action_save, (dlg, which) -> {
                    try {
                        // Leer campos
                        workout.setNombre(etNombre.getText().toString().trim());
                        workout.setDescripcion(etDescripcion.getText().toString().trim());
                        workout.setDuracion(Integer.parseInt(etDuracion.getText().toString().trim()));
                        // fecha ya actualizada en el listener

                        workout.setTipoEntrenamiento(
                                tipos.get(spinnerTipo.getSelectedItemPosition())
                        );

                        // Limpiar los ids para que JPA inserte nuevas relaciones
                        for (EntrenamientoEjercicioDTO rel : workout.getEntrenamientosEjercicios()) {
                            rel.setId(null);
                            for (SerieDTO s : rel.getSeries()) {
                                s.setId(null);
                            }
                        }

                        // Llamada al endpoint
                        onSave.accept(workout);

                    } catch (Exception e) {
                        Toast.makeText(parent.requireContext(),
                                R.string.error_actualizar, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
