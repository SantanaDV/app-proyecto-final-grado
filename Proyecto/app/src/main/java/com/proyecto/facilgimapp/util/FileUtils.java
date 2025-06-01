package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    /**
     * Copia el contenido de un Uri a un fichero local (en cacheDir),
     * usando el nombre deseado (desiredName), que ya incluye su extensión.
     */
    public static File copyUriToFile(Context ctx, Uri uri, String desiredName) {
        File dest = new File(ctx.getCacheDir(), desiredName);
        try (InputStream is = ctx.getContentResolver().openInputStream(uri);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return dest;
    }

}
