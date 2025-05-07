package com.proyecto.facilgimapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbarSettings.setTitle(R.string.title_settings);

        // Agregar pestañas
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_profile));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_security));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_appearance));

        // Cargar fragmento inicial
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsContainer, new ProfileSettingsFragment())
                .commit();

        // Cambiar fragmento al seleccionar pestaña
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected;
                switch (tab.getPosition()) {
                    case 1:
                        selected = new SecuritySettingsFragment();
                        break;
                    case 2:
                        selected = new AppearanceSettingsFragment();
                        break;
                    default:
                        selected = new ProfileSettingsFragment();
                }
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.settingsContainer, selected)
                        .commit();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
