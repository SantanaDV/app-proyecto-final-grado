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

public class UserOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onDarkModeToggled(boolean on);
        void onFontSizeChanged(int size);
        void onThemeColorSelected(int resId);
        void onLanguageChanged(String languageCode);
        void onChangePassword();
        void onManageUsers();
        void onClearPreferences();
    }

    private final Context ctx;
    private final List<UserOptionItem> items;
    private final Listener listener;

    public UserOptionsAdapter(Context ctx,
                              List<UserOptionItem> items,
                              Listener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    @NonNull @Override
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

    @Override public int getItemCount() { return items.size(); }

    class DarkVH extends RecyclerView.ViewHolder {
        private final SwitchCompat sw;
        DarkVH(View v) {
            super(v);
            sw = v.findViewById(R.id.switchDarkMode);
        }
        void bind() {
            boolean isOn = PreferenceManager.isDarkModeEnabled(ctx);
            sw.setChecked(isOn);
            sw.setOnCheckedChangeListener((btn, on) -> listener.onDarkModeToggled(on));
        }
    }

    class FontVH extends RecyclerView.ViewHolder {
        private final SeekBar sb;
        FontVH(View v) {
            super(v);
            sb = v.findViewById(R.id.seekBarFontSize);
        }
        void bind() {
            int curr = PreferenceManager.getFontSize(ctx);
            sb.setProgress(curr);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar s, int prog, boolean u) {
                    PreferenceManager.setFontSize(ctx, prog);
                    listener.onFontSizeChanged(prog);
                    // recrea para aplicar el nuevo fontScale
                    if (ctx instanceof Activity) ((Activity) ctx).recreate();
                }
                @Override public void onStartTrackingTouch(SeekBar s) {}
                @Override public void onStopTrackingTouch(SeekBar s) {}
            });
        }
    }


    class ThemeVH extends RecyclerView.ViewHolder {
        private final View green, blue, yellow;
        ThemeVH(View v) {
            super(v);
            green  = v.findViewById(R.id.colorGreen);
            blue   = v.findViewById(R.id.colorBlue);
            yellow = v.findViewById(R.id.colorYellow);
        }
        void bind() {
            int sel = PreferenceManager.getThemeColor(ctx);
            green .setAlpha(sel==R.drawable.circle_green   ? 1f : 0.5f);
            blue  .setAlpha(sel==R.drawable.circle_blue    ? 1f : 0.5f);
            yellow.setAlpha(sel==R.drawable.circle_yellow  ? 1f : 0.5f);
            green .setOnClickListener(v -> listener.onThemeColorSelected(R.drawable.circle_green));
            blue  .setOnClickListener(v -> listener.onThemeColorSelected(R.drawable.circle_blue));
            yellow.setOnClickListener(v -> listener.onThemeColorSelected(R.drawable.circle_yellow));
        }
    }

    class LangVH extends RecyclerView.ViewHolder {
        private final ImageView es, en;
        LangVH(View v) {
            super(v);
            es = v.findViewById(R.id.ivFlagEs);
            en = v.findViewById(R.id.ivFlagEn);
        }
        void bind() {
            String cur = PreferenceManager.getLanguage(ctx);
            es.setAlpha(cur.equals("es") ? 1f : 0.5f);
            en.setAlpha(cur.equals("en") ? 1f : 0.5f);
            es.setOnClickListener(v -> listener.onLanguageChanged("es"));
            en.setOnClickListener(v -> listener.onLanguageChanged("en"));
        }
    }

    class DefaultVH extends RecyclerView.ViewHolder {
        private final TextView tv;
        DefaultVH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvUserOption);
        }
        void bind(String text, Runnable action) {
            tv.setText(text);
            itemView.setOnClickListener(v -> action.run());
        }
    }
}
