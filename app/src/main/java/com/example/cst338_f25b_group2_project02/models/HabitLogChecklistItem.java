/**
 * <br>
 * Data model used by the ChecklistAdapter and MainActivity to represent a single
 * item in the user's daily habit checklist. This model wraps a {@link HabitLogs}
 * entity along with the resolved habit title, allowing the UI to easily display
 * both the name of the habit and whether it has been completed for the day.
 * <br><br>
 *
 * This class does not interact with the database directly; instead, the Activity
 * updates the underlying HabitLogs entry through the repository when the user
 * toggles completion.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.models;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;

/**
 * Lightweight UI model representing a single habit log entry in the daily
 * checklist. Contains the habit title for display, the associated HabitLogs
 * entity, and an in-memory completion state used by the checklist UI.
 */
public class HabitLogChecklistItem {

    /**
     * The underlying database log entry this UI item represents.
     */
    private final HabitLogs habitLog;

    /**
     * The resolved title of the habit, supplied by the Activity when constructing
     * checklist items.
     */
    private final String title;

    /**
     * Whether the habit is marked completed in the UI. May differ from database
     * state until the Activity persists the change via the repository.
     */
    private boolean completed;

    /**
     * Constructs a new checklist item based on the provided habit log and title.
     *
     * @param title    the display title of the habit
     * @param habitLog the underlying {@link HabitLogs} database entity
     */
    public HabitLogChecklistItem(String title, HabitLogs habitLog) {
        this.habitLog = habitLog;
        this.title = title;
        this.completed = habitLog.isCompleted();
    }

    // ------------------- UI Getters -------------------

    /**
     * @return the title of the habit
     */
    public String getHabitTitle() {
        return title;
    }

    /**
     * @return whether the habit is marked completed in the UI
     */
    public boolean isCompleted() {
        return completed;
    }

    // ------------------- UI Setters -------------------

    /**
     * Updates the in-memory completion state for this checklist item.
     * Does not update the database — that is handled by MainActivity
     * via the repository.
     *
     * @param completed the new completion state
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // ------------------- HabitLogs Accessors -------------------

    /**
     * @return the underlying {@link HabitLogs} entity
     */
    public HabitLogs getHabitLog() {
        return habitLog;
    }

    /**
     * @return the ID of the habit this log entry refers to
     */
    public int getHabitId() {
        return habitLog.getHabitId();
    }

    /**
     * @return the date associated with this habit log entry (ISO format)
     */
    public String getDate() {
        return habitLog.getDate();
    }
}
