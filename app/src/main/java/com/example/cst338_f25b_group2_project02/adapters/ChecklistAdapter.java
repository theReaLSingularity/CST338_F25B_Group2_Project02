package com.example.cst338_f25b_group2_project02.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.R;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter
        extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<Habits> habits = new ArrayList<>();

    // ✅ NEW: tracks how many habits are completed today
    private int completedCount = 0;

    // Constructor
    public ChecklistAdapter(List<Habits> habits) {
        if (habits != null) {
            this.habits = habits;
        }
    }

    // Called by MainActivity
    public void setHabits(List<Habits> habits) {
        this.habits = habits;
        completedCount = 0; // reset daily count on refresh
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Habits habit = habits.get(position);

        holder.textItemLabel.setText(habit.getTitle());

        // 🔁 Reset recycled state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(false);
        holder.textItemLabel.setPaintFlags(
                holder.textItemLabel.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
        );
        holder.textItemLabel.setTextColor(Color.BLACK);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                completedCount++;

                // ✖ Strike-through
                holder.textItemLabel.setPaintFlags(
                        holder.textItemLabel.getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG
                );

            } else {
                completedCount--;

                holder.textItemLabel.setPaintFlags(
                        holder.textItemLabel.getPaintFlags()
                                & ~Paint.STRIKE_THRU_TEXT_FLAG
                );
            }

            // 🎨 Color progression logic
            if (completedCount <= 0) {
                holder.textItemLabel.setTextColor(Color.BLACK);
            } else if (completedCount == 1) {
                holder.textItemLabel.setTextColor(Color.RED);
            } else if (completedCount <= 3) {
                holder.textItemLabel.setTextColor(Color.rgb(255, 165, 0)); // yellow/orange
            } else {
                holder.textItemLabel.setTextColor(Color.GREEN);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    // --------------------------
    // ViewHolder
    // --------------------------
    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView textItemLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkItem);
            textItemLabel = itemView.findViewById(R.id.textItemLabel);
        }
    }
}
