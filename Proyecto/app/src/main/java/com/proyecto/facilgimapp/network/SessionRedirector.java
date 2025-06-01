package com.proyecto.facilgimapp.network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.ui.activities.MainActivity;
/**
 * Clase encargada de redirigir al usuario a la pantalla de login (actividad principal) 
 * cuando su sesión ha sido cerrada o forzada a logout.
 * Muestra un mensaje de cierre de sesión y lanza la MainActivity con flags para limpiar
 * el historial de actividades y forzar el flujo de autenticación nuevamente.
 * 
 * @author: Francisco Santana
 */
public class SessionRedirector {
   /**
     * Muestra un mensaje indicando que la sesión se ha cerrado y redirige a MainActivity
     * con un extra que fuerza el logout. Limpia el back stack para evitar regresar a pantallas
     * protegidas.
     *
     * @param context Contexto desde el cual se invoca la redirección; se utiliza para mostrar
     *                el Toast y lanzar la actividad.
     */
    public static void redirectToLogin(Context context) {
        Toast.makeText(context, R.string.session_closed_by_logout, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("forceLogout", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
