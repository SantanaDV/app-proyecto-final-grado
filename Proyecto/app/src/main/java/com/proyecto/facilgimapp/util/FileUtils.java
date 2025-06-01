package com.proyecto.facilgimapp.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilidad para operaciones comunes con ficheros, como copiar un recurso {@link Uri}
 * a un archivo local dentro de <code>cacheDir</code>.
 * <p>
 * Este proveedor simplifica la copia de datos desde un Uri (por ejemplo, obtenido
 * mediante un selector de archivos) a un archivo temporal en la caché de la aplicación,
 * preservando la extensión especificada en el nombre deseado.
 * </p>
 *
 * Autor: Francisco Santana
 */
public class FileUtils {

    /**
     * Copia el contenido apuntado por un {@link Uri} a un fichero local creado en
     * el directorio de caché de la aplicación (<code>ctx.getCacheDir()</code>).
     * <p>
     * El parámetro <strong>desiredName</strong> debe incluir la extensión adecuada
     * (por ejemplo, "imagen.jpg" o "documento.pdf"). Si el fichero destino ya existe,
     * se sobrescribe. En caso de error durante la lectura o escritura, se devuelve <code>null</code>.
     * </p>
     *
     * @param ctx         Contexto de la aplicación, usado para acceder a <code>getCacheDir()</code>
     *                    y al <code>ContentResolver</code>.
     * @param uri         {@link Uri} de origen cuyos datos se desean copiar. Puede apuntar
     *                    a una imagen, documento u otro recurso accesible por el ContentResolver.
     * @param desiredName Nombre de fichero deseado para almacenar en el directorio de caché.
     *                    Debe contener la extensión correspondiente (por ejemplo, "foto.png").
     * @return Un objeto {@link File} que representa el nuevo fichero en cacheDir con el contenido
     *         copiado del Uri. Devuelve <code>null</code> si ocurre un error de E/S.
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
