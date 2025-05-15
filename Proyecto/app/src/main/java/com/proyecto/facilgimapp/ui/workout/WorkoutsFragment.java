package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentWorkoutsBinding;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SessionManager;
import com.proyecto.facilgimapp.viewmodel.WorkoutViewModel;

import java.util.ArrayList;

public class WorkoutsFragment extends Fragment {
    private FragmentWorkoutsBinding b;
    private WorkoutAdapter adapter;

    private WorkoutViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentWorkoutsBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(this).get(WorkoutViewModel.class);

        adapter = new WorkoutAdapter(new ArrayList<>(), id -> {
            Bundle args = new Bundle();
            args.putInt("workoutId", id);
            Navigation.findNavController(b.getRoot())
                    .navigate(R.id.action_workoutsFragment_to_workoutDetailFragment, args);
        });

        b.rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvWorkouts.setAdapter(adapter);

        vm.getWorkouts().observe(getViewLifecycleOwner(), list -> {
            Log.d("DEBUG", "Entrenamientos recibidos: " + (list != null ? list.size() : 0));
            adapter.updateList(list);
        });

        int userId = SessionManager.getUserId(requireContext());
        vm.loadWorkoutsByUserId(userId);

        // Agrega aquí la configuración del menú (filtro)
        configurarMenu();

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

    private void configurarMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_search, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                androidx.appcompat.widget.SearchView searchView =
                        (androidx.appcompat.widget.SearchView) searchItem.getActionView();

                searchView.setQueryHint("Buscar entrenamientos...");
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) { return false; }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

}
