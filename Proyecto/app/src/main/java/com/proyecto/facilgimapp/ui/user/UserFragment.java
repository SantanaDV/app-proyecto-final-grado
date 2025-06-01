package com.proyecto.facilgimapp.ui.user;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentUserBinding;
import com.proyecto.facilgimapp.model.entity.UserOptionItem;
import com.proyecto.facilgimapp.model.entity.UserOptionType;
import com.proyecto.facilgimapp.util.PreferenceManager;
import com.proyecto.facilgimapp.util.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment implements UserOptionsAdapter.Listener {
    private FragmentUserBinding binding;
    private UserOptionsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  Usuario
        binding.tvUsername.setText(SessionManager.getUsername(requireContext()));

        // RecyclerView + LayoutManager
        binding.rvUserOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserOptionsAdapter(
                requireContext(),
                getUserOptions(),
                this
        );
        binding.rvUserOptions.setAdapter(adapter);

        //  Switch tema sistema
        boolean useSystem = PreferenceManager.isUseSystemTheme(requireContext());
        binding.switchUseSystemTheme.setChecked(useSystem);
        applyDarkMode(useSystem);
        binding.switchUseSystemTheme.setOnCheckedChangeListener((btn, checked) -> {
            PreferenceManager.setUseSystemTheme(requireContext(), checked);
            applyDarkMode(checked);
            requireActivity().recreate();
        });

        //  Cerrar sesión
        binding.btnLogout.setOnClickListener(v -> {
            SessionManager.clearLoginOnly(requireContext());
            Toast.makeText(requireContext(),
                    R.string.session_closed, Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_userFragment_to_loginFragment);
        });
    }

    private void applyDarkMode(boolean useSystem) {
        if (useSystem) {
            // Sigo el tema actual del sistema
            int mode = requireContext().getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            boolean sysDark = (mode == Configuration.UI_MODE_NIGHT_YES);
            PreferenceManager.setDarkMode(requireContext(), sysDark);
        }
    }

    private List<UserOptionItem> getUserOptions() {
        var opts = new ArrayList<UserOptionItem>();
        opts.add(new UserOptionItem(UserOptionType.DARK_MODE));
        opts.add(new UserOptionItem(UserOptionType.FONT_SIZE));
        opts.add(new UserOptionItem(UserOptionType.THEME_COLOR));
        opts.add(new UserOptionItem(UserOptionType.LANGUAGE));
        opts.add(new UserOptionItem(UserOptionType.CHANGE_PASSWORD));
        if (SessionManager.isAdmin(requireContext())) {
            opts.add(new UserOptionItem(UserOptionType.MANAGE_USERS));
        }
        opts.add(new UserOptionItem(UserOptionType.CLEAR_PREFERENCES));
        return opts;
    }

    // Callbacks del adapter
    @Override public void onDarkModeToggled(boolean on) {
        // Al cambiar manualmente el modo oscuro desactivamos el usar tema del sistema
        PreferenceManager.setUseSystemTheme(requireContext(), false);
        binding.switchUseSystemTheme.setChecked(false);

        // Guardamos el dark mode y recreamos
        PreferenceManager.setDarkMode(requireContext(), on);
        requireActivity().recreate();
    }
    @Override
    public void onFontSizeChanged(int size) {
        PreferenceManager.setFontSize(requireContext(), size);
        requireActivity().recreate();
    }

    @Override
    public void onThemeColorSelected(int idx) {
        PreferenceManager.setThemeColorIndex(requireContext(), idx);
        requireActivity().recreate();
    }
    @Override public void onLanguageChanged(String code) {
        PreferenceManager.setLanguage(requireContext(), code);
        requireActivity().recreate();
    }
    @Override public void onChangePassword() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_changePasswordFragment);
    }
    @Override public void onManageUsers() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_userFragment_to_adminUserFragment);
    }
    @Override
    public void onClearPreferences() {
        PreferenceManager.clearAll(requireContext());
        PreferenceManager.setUseSystemTheme(requireContext(), true);
        PreferenceManager.setLanguage(requireContext(), "es");
        PreferenceManager.setFontSize(requireContext(), 2);
        // restablece al índice 0
        PreferenceManager.setThemeColorIndex(requireContext(), 0);
        applyDarkMode(true);
        Toast.makeText(requireContext(),
                R.string.preferencias_restablecidas, Toast.LENGTH_SHORT).show();
        requireActivity().recreate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
