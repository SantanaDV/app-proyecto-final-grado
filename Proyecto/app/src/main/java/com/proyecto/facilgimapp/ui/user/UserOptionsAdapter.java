package com.proyecto.facilgimapp.ui.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.ui.user.UserFragment;

import java.util.List;

public class UserOptionsAdapter extends RecyclerView.Adapter<UserOptionsAdapter.UserOptionViewHolder> {

    private final Context context;
    private final UserFragment fragment;
    private List<String> options;

    public UserOptionsAdapter(Context context, UserFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void setUserOptions(List<String> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_option, parent, false);
        return new UserOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOptionViewHolder holder, int position) {
        String option = options.get(position);
        holder.optionText.setText(option);

        holder.itemView.setOnClickListener(v -> fragment.onOptionSelected(option));
    }

    @Override
    public int getItemCount() {
        return options != null ? options.size() : 0;
    }

    public static class UserOptionViewHolder extends RecyclerView.ViewHolder {
        TextView optionText;

        public UserOptionViewHolder(View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.tvUserOption);
        }
    }
}
