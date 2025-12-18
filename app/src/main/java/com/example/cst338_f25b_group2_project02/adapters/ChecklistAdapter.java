package com.example.cst338_f25b_group2_project02.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.R;
import com.example.cst338_f25b_group2_project02.models.HabitLogChecklistItem;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<HabitLogChecklistItem> dayHabitLogs = new ArrayList<>();
    private final OnHabitCompleteClickListener completeClickListener;

    public interface OnHabitCompleteClickListener {
        void onHabitComplete(HabitLogChecklistItem item, boolean isChecked);
    }

    public ChecklistAdapter(OnHabitCompleteClickListener listener) {
        this.completeClickListener = listener;
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
        HabitLogChecklistItem item = dayHabitLogs.get(position);
        holder.textItemLabel.setText(item.getHabitTitle());

        holder.checkItem.setOnCheckedChangeListener(null);
        holder.checkItem.setChecked(item.isCompleted());
        applyStrikeThrough(holder.textItemLabel, item.isCompleted());

        holder.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            applyStrikeThrough(holder.textItemLabel, isChecked);
            completeClickListener.onHabitComplete(item, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return dayHabitLogs.size();
    }

    public void setHabitLogs(List<HabitLogChecklistItem> habitLogsList) {
        this.dayHabitLogs = habitLogsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkItem;
        TextView textItemLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkItem = itemView.findViewById(R.id.completedHabitCheckbox);
            textItemLabel = itemView.findViewById(R.id.completedHabitTextView);
        }
    }

    private void applyStrikeThrough(TextView textView, boolean strike) {
        if (strike) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public List<HabitLogChecklistItem> getHabitLogs() {
        return dayHabitLogs;
    }
}
