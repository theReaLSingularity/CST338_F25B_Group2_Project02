/**
 * <br>
 * RecyclerView adapter used in the EditingActivity to display the list of habits
 * belonging to the currently logged-in user. Each row contains the habit title
 * along with a checkbox used to mark the habit as "selected for deletion".
 * <br><br>
 *
 * The adapter does not directly perform deletion — instead it updates the
 * in-memory state of each {@link Habits} object. The hosting Activity may later
 * retrieve the list of selected habits and perform database operations through
 * the repository layer.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
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

/**
 * Adapter responsible for displaying a user's habits within a RecyclerView
 * in the EditingActivity. Each list item displays the habit title and a
 * checkbox used to indicate whether the habit should be deleted.
 */
public class EditingAdapter extends RecyclerView.Adapter<EditingAdapter.EditingViewHolder> {

    /**
     * List of habits currently displayed in the RecyclerView.
     */
    private List<Habits> habits = new ArrayList<>();

    /**
     * Listener used by the Activity to receive callbacks when the user
     * toggles the deletion checkbox for a habit.
     */
    private final OnHabitDeleteClickListener deleteClickListener;

    /**
     * Listener interface for notifying the hosting Activity when a habit
     * is selected or deselected for deletion.
     */
    public interface OnHabitDeleteClickListener {

        /**
         * Invoked when a habit's deletion checkbox is toggled.
         *
         * @param habit the {@link Habits} item whose selection state changed
         */
        void onHabitDelete(Habits habit);
    }

    /**
     * Constructs a new EditingAdapter.
     *
     * @param listener callback invoked whenever a habit is marked for deletion
     */
    public EditingAdapter(OnHabitDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    /**
     * Updates the displayed list of habits and refreshes the RecyclerView UI.
     *
     * @param newHabits the new list of {@link Habits} to display
     */
    public void setHabits(List<Habits> newHabits) {
        this.habits = newHabits;
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of habits displayed by the adapter.
     *
     * @return number of displayed habits
     */
    @Override
    public int getItemCount() {
        return habits.size();
    }

    /**
     * Inflates the habit row layout and creates a corresponding ViewHolder.
     *
     * @param parent the parent ViewGroup
     * @param viewType unused view type parameter
     * @return a new {@link EditingViewHolder}
     */
    @NonNull
    @Override
    public EditingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_item, parent, false);
        return new EditingViewHolder(view);
    }

    /**
     * Binds a habit's data to the row UI, including setting the title and the
     * current deletion selection state. The checkbox listener updates the in-memory
     * {@link Habits} object when toggled.
     *
     * @param holder ViewHolder holding row UI references
     * @param position position of the habit in the list
     */
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
                deleteClickListener.onHabitDelete(h);
            }
        });
    }

    /**
     * ViewHolder class that stores references to the TextView displaying the habit
     * title and the CheckBox used to mark the habit for deletion.
     */
    public static class EditingViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView displaying the name of the habit.
         */
        TextView habit;

        /**
         * CheckBox used to toggle whether the habit should be deleted.
         */
        CheckBox checkBox;

        /**
         * Constructs a new EditingViewHolder and binds UI elements.
         *
         * @param itemView root view of the habit list item
         */
        public EditingViewHolder(@NonNull View itemView) {
            super(itemView);
            habit = itemView.findViewById(R.id.tv_name_item);
            checkBox = itemView.findViewById(R.id.habitEditCheckbox);
        }
    }

    /**
     * Returns the current list of habits displayed by the adapter. This is used
     * by the Activity to determine which habits have been selected for deletion.
     *
     * @return the list of {@link Habits} objects managed by the adapter
     */
    public List<Habits> getHabits() {
        return habits;
    }
}
