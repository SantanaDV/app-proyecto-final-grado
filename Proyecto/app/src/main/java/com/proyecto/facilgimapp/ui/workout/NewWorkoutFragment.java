package com.proyecto.facilgimapp.ui.workout;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentNewWorkoutBinding;
import com.proyecto.facilgimapp.model.dto.EntrenamientoDTO;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;
import com.proyecto.facilgimapp.viewmodel.WorkoutViewModel;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class NewWorkoutFragment extends Fragment {
    private FragmentNewWorkoutBinding b;
    private TypeViewModel typeVM;
    private WorkoutViewModel workoutVM;
    private LocalDate selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup ctr, Bundle bdl) {
        b = FragmentNewWorkoutBinding.inflate(inf, ctr, false);
        typeVM = new ViewModelProvider(this).get(TypeViewModel.class);
        workoutVM = new ViewModelProvider(this).get(WorkoutViewModel.class);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle bdl) {
        // 1) Spinner de tipos: carga y adapta
        typeVM.getTypes().observe(getViewLifecycleOwner(), types -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    types.stream().map(t -> t.getNombre()).toList()
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            b.spinnerType.setAdapter(adapter);
        });
        typeVM.loadTypes();

        // 2) Selector de fecha
        b.btnPickDate.setOnClickListener(__ -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (view, y, m, d) -> {
                        selectedDate = LocalDate.of(y, m+1, d);
                        b.btnPickDate.setText(selectedDate.toString());
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // 3) “Siguiente” a lista de ejercicios (pasamos EntrenamientoDTO parcial)
        b.btnNextToExercises.setOnClickListener(__ -> {
            EntrenamientoDTO dto = new EntrenamientoDTO();
            dto.setNombre(b.etName.getText().toString());
            dto.setDescripcion(b.etDescription.getText().toString());
            dto.setDuracion(Integer.parseInt(b.etDuration.getText().toString()));
            dto.setFechaEntrenamiento(selectedDate);
            // tipoSeleccionado -> buscamos su id en TypeViewModel.getTypes()
            long typeId = typeVM.getTypes().getValue()
                    .get(b.spinnerType.getSelectedItemPosition())
                    .getId();
            dto.setTipoEntrenamientoId(typeId);
            dto.setUsuarioId(SessionManager.getUserId(requireContext()));

            // Navegar a WorkoutExercisesFragment (pendiente de crear) llevando el DTO
            Bundle args = new Bundle();
            args.putSerializable("newWorkoutDTO", dto);
            Navigation.findNavController(b.getRoot())
                    .navigate(R.id.action_newWorkoutFragment_to_workoutExercisesFragment, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
