/**
 * <br>
 * RecyclerView adapter used to display a daily checklist of habits for the currently
 * logged-in user. Each row shows a habit title and a checkbox indicating whether
 * the habit has been completed for the selected day.
 * <br><br>
 * The adapter works with {@link com.example.cst338_f25b_group2_project02.models.HabitLogChecklistItem}
 * objects and uses a callback interface to notify the hosting Activity when a habit's
 * completion state has changed, so the Activity can update the underlying database.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
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

/**
 * Adapter responsible for binding a list of {@link HabitLogChecklistItem} objects
 * to checklist rows in a RecyclerView. Each row consists of a checkbox and the
 * habit title, with a strike-through applied when the habit is marked complete.
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    /**
     * The list of habit log items representing the current day's habits.
     */
    private List<HabitLogChecklistItem> dayHabitLogs = new ArrayList<>();

    /**
     * Callback listener used to notify the hosting component when a habit's
     * completion status is toggled.
     */
    private final OnHabitCompleteClickListener completeClickListener;

    /**
     * Listener interface used to communicate habit completion changes back
     * to the Activity or Fragment that owns the adapter.
     */
    public interface OnHabitCompleteClickListener {

        /**
         * Called when the user checks or unchecks a habit in the checklist.
         *
         * @param item      the {@link HabitLogChecklistItem} that was toggled
         * @param isChecked {@code true} if the habit is now marked completed,
         *                  {@code false} otherwise
         */
        void onHabitComplete(HabitLogChecklistItem item, boolean isChecked);
    }

    /**
     * Constructs a new ChecklistAdapter.
     *
     * @param listener the callback invoked whenever a habit's completion
     *                 state changes
     */
    public ChecklistAdapter(OnHabitCompleteClickListener listener) {
        this.completeClickListener = listener;
    }

    /**
     * Inflates the checklist row layout and creates a new {@link ViewHolder}
     * instance to hold references to the row views.
     *
     * @param parent   the parent ViewGroup into which the new view will be added
     * @param viewType the type of the new view (unused in this adapter)
     * @return a new {@link ViewHolder} containing the inflated row views
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds a {@link HabitLogChecklistItem} to a row in the checklist. Sets the
     * habit title, checkbox state, and strike-through for the row's TextView,
     * and registers a listener to propagate completion changes via the callback.
     *
     * @param holder   the {@link ViewHolder} containing the row views
     * @param position the zero-based index of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitLogChecklistItem item = dayHabitLogs.get(position);
        holder.textItemLabel.setText(item.getHabitTitle());

        // Avoid triggering the listener when restoring the checked state
        holder.checkItem.setOnCheckedChangeListener(null);
        holder.checkItem.setChecked(item.isCompleted());
        applyStrikeThrough(holder.textItemLabel, item.isCompleted());

        holder.checkItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            applyStrikeThrough(holder.textItemLabel, isChecked);
            completeClickListener.onHabitComplete(item, isChecked);
        });
    }

    /**
     * Returns the total number of checklist items currently managed by the adapter.
     *
     * @return the number of items in the checklist
     */
    @Override
    public int getItemCount() {
        return dayHabitLogs.size();
    }

    /**
     * Replaces the current list of habit log checklist items and refreshes
     * the RecyclerView so that the new data is displayed.
     *
     * @param habitLogsList the new list of {@link HabitLogChecklistItem} objects
     *                      representing the current day's habits
     */
    public void setHabitLogs(List<HabitLogChecklistItem> habitLogsList) {
        this.dayHabitLogs = habitLogsList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder that holds references to the row views for a single
     * checklist item (checkbox and habit title TextView).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Checkbox used to mark a habit as completed or not completed.
         */
        CheckBox checkItem;

        /**
         * TextView displaying the habit title for this row.
         */
        TextView textItemLabel;

        /**
         * Constructs a new ViewHolder and binds the row views.
         *
         * @param itemView the root view of the checklist row
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkItem = itemView.findViewById(R.id.completedHabitCheckbox);
            textItemLabel = itemView.findViewById(R.id.completedHabitTextView);
        }
    }

    /**
     * Applies or removes a strike-through effect on the provided TextView based
     * on the {@code strike} parameter. Used to visually indicate that a habit
     * has been completed.
     *
     * @param textView the TextView to modify
     * @param strike   {@code true} to apply strike-through, {@code false} to remove it
     */
    private void applyStrikeThrough(TextView textView, boolean strike) {
        if (strike) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /**
     * Returns the current list of {@link HabitLogChecklistItem} objects backing
     * the adapter. This can be used by the hosting Activity or Fragment if it
     * needs to inspect the current in-memory state of the checklist.
     *
     * @return the list of habit log checklist items displayed by this adapter
     */
    public List<HabitLogChecklistItem> getHabitLogs() {
        return dayHabitLogs;
    }
}
