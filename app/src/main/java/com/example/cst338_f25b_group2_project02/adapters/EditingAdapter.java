package com.example.cst338_f25b_group2_project02.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.R;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import java.util.ArrayList;
import java.util.List;

public class EditingAdapter extends RecyclerView.Adapter<EditingAdapter.EditingViewHolder> {

    private List<Habits> habits = new ArrayList<>();
    private final OnHabitDeleteClickListener deleteClickListener;

    public interface OnHabitDeleteClickListener {
        void onHabitDelete(Habits habit);
    }

    public EditingAdapter(OnHabitDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setHabits(List<Habits> newHabits) {
        this.habits = newHabits;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    @NonNull
    @Override
    public EditingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_item, parent, false);
        return new EditingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditingViewHolder holder, int position) {
        Habits habit = habits.get(position);
        holder.habit.setText(habit.getTitle());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(habit.isSelectedForDeletion());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                Habits h = habits.get(adapterPos);
                h.setSelectedForDeletion(isChecked);
            }
        });
    }

    public static class EditingViewHolder extends RecyclerView.ViewHolder {
        TextView habit;
        CheckBox checkBox;

        public EditingViewHolder(@NonNull View itemView) {
            super(itemView);
            habit = itemView.findViewById(R.id.tv_name_item);
            checkBox = itemView.findViewById(R.id.habitEditCheckbox);
        }
    }

    public List<Habits> getHabits() {
        return habits;
    }
}
