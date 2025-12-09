package com.example.cst338_f25b_group2_project02;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private final List<String> items;
    private final boolean[] checkedState;

    public ChecklistAdapter(List<String> items) {
        this.items = items;
        this.checkedState = new boolean[items.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentItem = items.get(position);
        holder.textItemLabel.setText(currentItem);

        holder.checkItem.setOnCheckedChangeListener(null);
        holder.checkItem.setChecked(checkedState[position]);

        applyStrikeThrough(holder.textItemLabel, checkedState[position]);

        holder.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedState[position] = isChecked;
            applyStrikeThrough(holder.textItemLabel, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkItem;
        TextView textItemLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkItem = itemView.findViewById(R.id.checkItem);
            textItemLabel = itemView.findViewById(R.id.textItemLabel);
        }
    }

    private void applyStrikeThrough(TextView textView, boolean strike) {
        if (strike) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
