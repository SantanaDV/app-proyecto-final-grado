package com.proyecto.facilgimapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentAppearanceSettingsBinding;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SimpleSeekBarListener;

public class AppearanceSettingsFragment extends Fragment {
    private FragmentAppearanceSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppearanceSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Oscuro/Claro
        boolean dark = PreferenceManager.isDarkModeEnabled(requireContext());
        binding.switchDarkMode.setChecked(dark);
        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) ->
                PreferenceManager.setDarkMode(requireContext(), isChecked)
        );  // :contentReference[oaicite:6]{index=6}:contentReference[oaicite:7]{index=7}

        // Tamaño de letra
        int size = PreferenceManager.getFontSize(requireContext());
        binding.seekBarFontSize.setProgress(size);
        binding.seekBarFontSize.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                PreferenceManager.setFontSize(requireContext(), seekBar.getProgress());
            }
        });

        // Tema de color
        binding.colorGreen.setOnClickListener(v ->
                PreferenceManager.setAppColor(requireContext(), R.style.Theme_Green)
        );
        binding.colorBlue.setOnClickListener(v ->
                PreferenceManager.setAppColor(requireContext(), R.style.Theme_Blue)
        );
        binding.colorRed.setOnClickListener(v ->
                PreferenceManager.setAppColor(requireContext(), R.style.Theme_Red)
        );
    }
}
