package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutsBinding;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.WorkoutViewModel;

public class WorkoutsFragment extends Fragment {
    private FragmentWorkoutsBinding b;
    private WorkoutViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutsBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(WorkoutViewModel.class);

        b.rvWorkouts.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        vm.getWorkouts().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                Log.d("DEBUG", "Entrenamientos recibidos: " + list.size()); // Verificar cantidad de entrenamientos
                WorkoutAdapter adapter = new WorkoutAdapter(list, id -> {
                    Bundle args = new Bundle();
                    args.putInt("workoutId", id);
                    Navigation.findNavController(b.getRoot())
                            .navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, args);
                });
                b.rvWorkouts.setAdapter(adapter);
            } else {
                // Si no hay entrenamientos, mostrar un mensaje
                Log.d("DEBUG", "No hay entrenamientos para este usuario.");
            }
        });

        int userId = SessionManager.getUserId(requireContext());  // Asegúrate de que este valor es correcto
        Log.d("DEBUG", "Usuario ID: " + userId);  // Aquí
        vm.loadWorkoutsByUserId(userId);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón + Nuevo entrenamiento
        b.fabNewWorkout.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_workoutsFragment_to_newWorkoutFragment)
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
