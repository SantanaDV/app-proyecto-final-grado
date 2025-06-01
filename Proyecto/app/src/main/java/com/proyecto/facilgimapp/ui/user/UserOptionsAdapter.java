package com.proyecto.facilgimapp.ui.user;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.model.entity.UserOptionItem;
import com.proyecto.facilgimapp.model.entity.UserOptionType;
import com.proyecto.facilgimapp.util.PreferenceManager;

import java.util.List;

/**
 * Adaptador que presenta una lista de opciones de configuración del usuario en un RecyclerView.
 * Cada tipo de opción (modo oscuro, tamaño de fuente, tema, idioma, etc.) se representa con un
 * ViewHolder específico. Las interacciones se delegan a un {@link Listener}.
 * <p>
 * Los tipos de opciones se definen en {@link UserOptionType}, y cada elemento en la lista
 * es un {@link UserOptionItem} que contiene dicho tipo. El adaptador crea el ViewHolder
 * apropiado según el tipo y delega los eventos mediante la interfaz {@link Listener}.
 * </p>
 * 
 * @author Francisco Santana
 */
public class UserOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Interfaz para manejar las interacciones del usuario con las distintas opciones:
     * modo oscuro, tamaño de fuente, color de tema, idioma, cambio de contraseña, gestión de usuarios
     * y restablecimiento de preferencias.
     */
    public interface Listener {
        /**
         * Se invoca cuando el usuario alterna el modo oscuro.
         *
         * @param on {@code true} para activar el modo oscuro, {@code false} para desactivarlo.
         */
        void onDarkModeToggled(boolean on);

        /**
         * Se invoca cuando el usuario cambia el tamaño de fuente.
         *
         * @param size Valor numérico que indica el nuevo tamaño de fuente.
         */
        void onFontSizeChanged(int size);

        /**
         * Se invoca cuando el usuario selecciona un nuevo color de tema.
         *
         * @param idx Índice del color de tema seleccionado.
         */
        void onThemeColorSelected(int idx);

        /**
         * Se invoca cuando el usuario cambia el idioma de la aplicación.
         *
         * @param languageCode Código de idioma seleccionado (por ejemplo, "es" o "en").
         */
        void onLanguageChanged(String languageCode);

        /**
         * Se invoca cuando el usuario elige la opción de cambiar su contraseña.
         */
        void onChangePassword();

        /**
         * Se invoca cuando el usuario elige la opción de gestionar otros usuarios (solo administradores).
         */
        void onManageUsers();

        /**
         * Se invoca cuando el usuario solicita restablecer todas las preferencias a sus valores predeterminados.
         */
        void onClearPreferences();
    }

    private final Context ctx;
    private final List<UserOptionItem> items;
    private final Listener listener;

    /**
     * Constructor que inicializa el adaptador con el contexto, la lista de opciones a mostrar
     * y el listener para manejar las acciones del usuario.
     *
     * @param ctx      Contexto de la aplicación, utilizado para acceder a recursos y preferencias.
     * @param items    Lista de {@link UserOptionItem} que define qué opciones mostrar.
     * @param listener Implementación de {@link Listener} que procesará las interacciones.
     */
    public UserOptionsAdapter(Context ctx,
                              List<UserOptionItem> items,
                              Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    /**
     * Retorna el tipo de vista basado en el orden del {@link UserOptionType} del elemento.
     *
     * @param position Posición del elemento en la lista.
     * @return Un entero que corresponde al ordinal del {@link UserOptionType}.
     */
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    /**
     * Crea el ViewHolder adecuado según el tipo de opción especificado por getItemViewType.
     * Infla el layout correspondiente y devuelve la instancia de ViewHolder.
     *
     * @param parent   Contenedor padre donde se insertará la vista.
     * @param viewType Tipo de vista, corresponde al ordinal de {@link UserOptionType}.
     * @return Instancia de {@link RecyclerView.ViewHolder} apropiada para ese tipo.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserOptionType type = UserOptionType.values()[viewType];
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        switch (type) {
            case DARK_MODE:
                return new DarkVH(inf.inflate(R.layout.item_option_dark_mode, parent, false));
            case FONT_SIZE:
                return new FontVH(inf.inflate(R.layout.item_option_font_size, parent, false));
            case THEME_COLOR:
                return new ThemeVH(inf.inflate(R.layout.item_option_theme_color, parent, false));
            case LANGUAGE:
                return new LangVH(inf.inflate(R.layout.item_option_language, parent, false));
            case CHANGE_PASSWORD:
            case MANAGE_USERS:
            case CLEAR_PREFERENCES:
            default:
                return new DefaultVH(inf.inflate(R.layout.item_option_default, parent, false));
        }
    }

    /**
     * Vincula los datos y configura el ViewHolder según el tipo de opción:
     * <ul>
     *     <li>Modo oscuro: Switch</li>
     *     <li>Tamaño de fuente: SeekBar</li>
     *     <li>Color de tema: varias vistas de color</li>
     *     <li>Idioma: banderas para seleccionar idioma</li>
     *     <li>Opciones por defecto: texto y acción correspondiente</li>
     * </ul>
     *
     * @param vh  ViewHolder que contiene las vistas del ítem.
     * @param pos Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int pos) {
        UserOptionType type = items.get(pos).getType();
        switch (type) {
            case DARK_MODE:
                ((DarkVH) vh).bind();
                break;
            case FONT_SIZE:
                ((FontVH) vh).bind();
                break;
            case THEME_COLOR:
                ((ThemeVH) vh).bind();
                break;
            case LANGUAGE:
                ((LangVH) vh).bind();
                break;
            case CHANGE_PASSWORD:
                ((DefaultVH) vh).bind(
                        ctx.getString(R.string.option_change_password),
                        listener::onChangePassword
                );
                break;
            case MANAGE_USERS:
                ((DefaultVH) vh).bind(
                        ctx.getString(R.string.option_manage_users),
                        listener::onManageUsers
                );
                break;
            case CLEAR_PREFERENCES:
                ((DefaultVH) vh).bind(
                        ctx.getString(R.string.option_clear_preferences),
                        listener::onClearPreferences
                );
                break;
        }
    }

    /**
     * Retorna el número total de elementos en la lista de opciones.
     *
     * @return Cantidad de {@link UserOptionItem} disponibles.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder para la opción de modo oscuro, que presenta un SwitchCompat.
     * <p>
     * Al enlazar, obtiene el estado actual de {@link PreferenceManager#isDarkModeEnabled(Context)}
     * y configura el listener para alternar el modo oscuro mediante {@link Listener#onDarkModeToggled(boolean)}.
     * </p>
     */
    class DarkVH extends RecyclerView.ViewHolder {
        private final SwitchCompat sw;

        /**
         * Constructor que asocia la vista del Switch al ViewHolder.
         *
         * @param v Vista inflada de R.layout.item_option_dark_mode.
         */
        DarkVH(View v) {
            super(v);
            sw = v.findViewById(R.id.switchDarkMode);
        }

        /**
         * Enlaza el estado actual y configura el listener del Switch.
         */
        void bind() {
            boolean isOn = PreferenceManager.isDarkModeEnabled(ctx);
            sw.setChecked(isOn);
            sw.setOnCheckedChangeListener((btn, on) -> listener.onDarkModeToggled(on));
        }
    }

    /**
     * ViewHolder para la opción de tamaño de fuente, que presenta un SeekBar.
     * <p>
     * Al enlazar, obtiene el tamaño actual de {@link PreferenceManager#getFontSize(Context)},
     * ajusta la posición del SeekBar y configura el listener para cambios de progreso.
     * </p>
     */
    class FontVH extends RecyclerView.ViewHolder {
        private final SeekBar sb;

        /**
         * Constructor que asocia la vista del SeekBar al ViewHolder.
         *
         * @param v Vista inflada de R.layout.item_option_font_size.
         */
        FontVH(View v) {
            super(v);
            sb = v.findViewById(R.id.seekBarFontSize);
        }

        /**
         * Enlaza el estado actual del tamaño de fuente y configura el listener.
         * Al cambiar, guarda la nueva preferencia y notifica al {@link Listener}.
         * También recrea la actividad para aplicar el cambio de escala de fuente.
         */
        void bind() {
            int curr = PreferenceManager.getFontSize(ctx);
            sb.setProgress(curr);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar s, int prog, boolean u) {
                    PreferenceManager.setFontSize(ctx, prog);
                    listener.onFontSizeChanged(prog);
                    if (ctx instanceof Activity) ((Activity) ctx).recreate();
                }
                @Override public void onStartTrackingTouch(SeekBar s) {}
                @Override public void onStopTrackingTouch(SeekBar s) {}
            });
        }
    }

    /**
     * ViewHolder para la opción de color de tema, que presenta tres vistas clicables.
     * <p>
     * Al enlazar, resalta la opción seleccionada mediante alpha y configura los listeners
     * para seleccionar un nuevo índice de color, notificando al {@link Listener}.
     * </p>
     */
    class ThemeVH extends RecyclerView.ViewHolder {
        private final View opt1, opt2, opt3;

        /**
         * Constructor que asocia las tres vistas de color al ViewHolder.
         *
         * @param v Vista inflada de R.layout.item_option_theme_color.
         */
        ThemeVH(View v) {
            super(v);
            opt1 = v.findViewById(R.id.colorOption1);
            opt2 = v.findViewById(R.id.colorOption2);
            opt3 = v.findViewById(R.id.colorOption3);
        }

        /**
         * Enlaza el color de tema actual y configura el listener para cada opción.
         * Ajusta la opacidad para indicar la selección.
         */
        void bind() {
            int sel = PreferenceManager.getThemeColorIndex(itemView.getContext());
            View[] opts = { opt1, opt2, opt3 };
            for (int i = 0; i < opts.length; i++) {
                opts[i].setAlpha(sel == i ? 1f : 0.5f);
                final int idx = i;
                opts[i].setOnClickListener(v ->
                        listener.onThemeColorSelected(idx)
                );
            }
        }
    }

    /**
     * ViewHolder para la opción de idioma, que presenta dos ImageView con banderas.
     * <p>
     * Al enlazar, obtiene el idioma actual de {@link PreferenceManager#getLanguage(Context)}
     * y ajusta la opacidad de las banderas. Configura los listeners para cambiar el idioma
     * a "es" o "en", notificando al {@link Listener}.
     * </p>
     */
    class LangVH extends RecyclerView.ViewHolder {
        private final ImageView es, en;

        /**
         * Constructor que asocia las vistas de las banderas al ViewHolder.
         *
         * @param v Vista inflada de R.layout.item_option_language.
         */
        LangVH(View v) {
            super(v);
            es = v.findViewById(R.id.ivFlagEs);
            en = v.findViewById(R.id.ivFlagEn);
        }

        /**
         * Enlaza el idioma actual y configura los listeners para cambiar a "es" o "en".
         */
        void bind() {
            String cur = PreferenceManager.getLanguage(ctx);
            es.setAlpha(cur.equals("es") ? 1f : 0.5f);
            en.setAlpha(cur.equals("en") ? 1f : 0.5f);
            es.setOnClickListener(v -> listener.onLanguageChanged("es"));
            en.setOnClickListener(v -> listener.onLanguageChanged("en"));
        }
    }

    /**
     * ViewHolder genérico para opciones que solo requieren un texto y una acción al hacer clic.
     * <p>
     * Los tipos que usan este ViewHolder son: cambio de contraseña, gestión de usuarios,
     * y restablecer preferencias. El layout es R.layout.item_option_default.
     * </p>
     */
    class DefaultVH extends RecyclerView.ViewHolder {
        private final TextView tv;

        /**
         * Constructor que asocia el TextView al ViewHolder.
         *
         * @param v Vista inflada de R.layout.item_option_default.
         */
        DefaultVH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvUserOption);
        }

        /**
         * Enlaza el texto a mostrar y la acción a ejecutar al hacer clic.
         *
         * @param text   Cadena a mostrar en el TextView.
         * @param action Runnable que se ejecutará al pulsar el elemento.
         */
        void bind(String text, Runnable action) {
            tv.setText(text);
            itemView.setOnClickListener(v -> action.run());
        }
    }
}
