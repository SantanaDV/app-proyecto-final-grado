// src/main/java/com/proyecto/facilgimapp/util/SessionManager.java
package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.proyecto.facilgimapp.R;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SessionManager {
    private static final String PREFS_NAME       = "facilgim_session";
    private static final String KEY_TOKEN        = "key_token";
    private static final String KEY_USERNAME     = "key_username";
    private static final String KEY_AUTHORITIES  = "key_authorities";
    private static final String KEY_USER_ID      = "key_user_id";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveLoginData(Context ctx,
                                     String token,
                                     String username,
                                     List<String> authorities,
                                     int userId) {
        prefs(ctx).edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USERNAME, username)
                .putString(KEY_AUTHORITIES, String.join(",", authorities))
                .putInt(KEY_USER_ID, userId)
                .apply();
    }

    public static String getToken(Context ctx) {
        return prefs(ctx).getString(KEY_TOKEN, null);
    }

    public static String getUsername(Context ctx) {
        return prefs(ctx).getString(KEY_USERNAME, null);
    }

    public static List<String> getAuthorities(Context ctx) {
        String csv = prefs(ctx).getString(KEY_AUTHORITIES, "");
        if (csv.isEmpty()) return Collections.emptyList();
        return Arrays.asList(csv.split(","));
    }

    public static int getUserId(Context ctx) {
        return prefs(ctx).getInt(KEY_USER_ID, -1);
    }

    public static boolean isAdmin(Context ctx) {
        for (String role : getAuthorities(ctx)) {
            if (role.equals("ROLE_ADMIN") || role.equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }

    public static void clearSession(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }
}
