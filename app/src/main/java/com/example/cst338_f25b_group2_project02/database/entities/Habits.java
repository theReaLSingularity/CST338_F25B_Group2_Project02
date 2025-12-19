/**
 * <br>
 * Represents a habit created by a user within the Habit Builder application.
 * Habits contain information such as title, category, start and end dates,
 * and whether the habit is currently active.
 * <br><br>
 *
 * This entity is stored in the Room database table defined by
 * {@link HabitBuilderDatabase#HABITS_TABLE}. Each habit is assigned an
 * auto-generated primary key and is associated with both a user and a category.
 * <br><br>
 *
 * This class also includes an {@code @Ignore} field, {@code selectedForDeletion},
 * used strictly for in-memory UI selection logic within RecyclerView adapters.
 * It is not persisted in the database.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

/**
 * Entity class modeling a user-created habit. Habits may be active or inactive,
 * belong to a specific category, and persist between sessions through Room.
 */
@Entity(tableName = HabitBuilderDatabase.HABITS_TABLE)
public class Habits {

    /**
     * Auto-generated unique identifier for the habit.
     */
    @PrimaryKey(autoGenerate = true)
    private int habitId;

    /**
     * The ID of the user who owns this habit.
     */
    private int userId;

    /**
     * The ID of the category to which this habit belongs.
     */
    private int categoryId;

    /**
     * Human-readable title of the habit (e.g., "Drink More Water").
     */
    private String title;

    /**
     * Start date of the habit, stored as a string in ISO format (YYYY-MM-DD).
     */
    private String startDate;

    /**
     * End date of the habit, stored as a string in ISO format (YYYY-MM-DD).
     */
    private String endDate;

    /**
     * Indicates whether the habit is currently active.
     */
    private boolean isActive;

    /**
     * UI-only field used to temporarily mark a habit as selected for deletion.
     * Ignored by Room and not persisted.
     */
    @Ignore
    private boolean selectedForDeletion;

    /**
     * Constructs a new habit with the required fields. The habit ID is generated
     * automatically by Room upon insertion.
     *
     * @param userId     the ID of the user who owns this habit
     * @param categoryId the ID of the category associated with this habit
     * @param title      the title or name of the habit
     * @param startDate  the starting date of the habit (ISO string format)
     * @param endDate    the ending date of the habit (ISO string format)
     * @param isActive   whether the habit is currently active
     */
    public Habits(int userId, int categoryId, String title, String startDate,
                  String endDate, boolean isActive) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.selectedForDeletion = false;
    }

    // ------------------- Getters -------------------

    /**
     * @return the unique ID of the habit
     */
    public int getHabitId() { return habitId; }

    /**
     * @return the ID of the user who owns this habit
     */
    public int getUserId() { return userId; }

    /**
     * @return the ID of the category this habit belongs to
     */
    public int getCategoryId() { return categoryId; }

    /**
     * @return the title of the habit
     */
    public String getTitle() { return title; }

    /**
     * @return the habit's start date
     */
    public String getStartDate() { return startDate; }

    /**
     * @return the habit's end date
     */
    public String getEndDate() { return endDate; }

    /**
     * @return {@code true} if the habit is active; {@code false} otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * @return whether this habit is currently marked for deletion (UI only)
     */
    public boolean isSelectedForDeletion() { return selectedForDeletion; }

    // ------------------- Setters -------------------

    /**
     * Sets the habit ID. Normally assigned automatically by Room.
     */
    public void setHabitId(int habitId) { this.habitId = habitId; }

    /**
     * Updates the user ID associated with this habit.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Updates the category ID associated with this habit.
     */
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    /**
     * Sets the title of the habit.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Sets the start date of the habit.
     */
    public void setStartDate(String startDate) { this.startDate = startDate; }

    /**
     * Sets the end date of the habit.
     */
    public void setEndDate(String endDate) { this.endDate = endDate; }

    /**
     * Sets the active state of the habit.
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Sets whether this habit is selected for deletion (UI-only).
     */
    public void setSelectedForDeletion(boolean selectedForDeletion) {
        this.selectedForDeletion = selectedForDeletion;
    }

    // ------------------- Equality -------------------

    /**
     * Determines whether two {@code Habits} objects are equal based on all
     * essential persisted fields.
     *
     * @param o the object to compare with
     * @return {@code true} if both objects represent the same habit
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Habits habits = (Habits) o;
        return habitId == habits.habitId &&
                userId == habits.userId &&
                categoryId == habits.categoryId &&
                isActive == habits.isActive &&
                Objects.equals(title, habits.title) &&
                Objects.equals(startDate, habits.startDate) &&
                Objects.equals(endDate, habits.endDate);
    }

    /**
     * Computes a hash code based on all core fields of the habit.
     *
     * @return a hash code value for this habit
     */
    @Override
    public int hashCode() {
        return Objects.hash(habitId, userId, categoryId, title, startDate, endDate, isActive);
    }
}
