package com.example.cst338_f25b_group2_project02.adapters;

import android.graphics.Paint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.databinding.ItemChecklistBinding;
import com.example.cst338_f25b_group2_project02.models.ChecklistItem;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private final List<ChecklistItem> items;
    private Runnable onChecklistChanged;

    public ChecklistAdapter(List<ChecklistItem> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        public ViewHolder(ItemChecklistBinding binding) {
            super(binding.getRoot());
            checkBox = binding.checkboxItem;
        }
    }

    @NonNull
    @Override
    public ChecklistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChecklistBinding binding = ItemChecklistBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistAdapter.ViewHolder holder, int position) {
        ChecklistItem item = items.get(position);

        holder.checkBox.setText(item.getTitle());
        holder.checkBox.setChecked(item.isCompleted());

        // Visual style
        if (item.isCompleted()) {
            holder.checkBox.setTextColor(Color.parseColor("#2ECC71"));
            holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.checkBox.setTextColor(Color.BLACK);
            holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            notifyItemChanged(position);
            if (onChecklistChanged != null) onChecklistChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnChecklistChanged(Runnable listener) {
        this.onChecklistChanged = listener;
    }
}
