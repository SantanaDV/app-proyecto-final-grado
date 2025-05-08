package com.proyecto.facilgimapp.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.proyecto.facilgimapp.R;

public class WorkoutExercisesFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup ctr,
                             Bundle bdl) {
        return inf.inflate(R.layout.fragment_workout_exercises, ctr, false);
    }
}
