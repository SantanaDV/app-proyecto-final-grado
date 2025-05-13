package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.proyecto.facilgimapp.ui.MainActivity;

public class SessionRedirector {

    public static void redirectToLogin(Context context) {
        Toast.makeText(context, "Sesión expirada. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("forceLogout", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
