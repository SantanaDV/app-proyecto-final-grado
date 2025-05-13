package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.ui.MainActivity;

public class SessionRedirector {

    public static void redirectToLogin(Context context) {
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.runOnUiThread(() -> {
                Toast.makeText(context, "Sesión expirada. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show();

                NavController navController = activity.getNavController();
                navController.navigate(R.id.loginFragment);
            });
        }
    }
}
