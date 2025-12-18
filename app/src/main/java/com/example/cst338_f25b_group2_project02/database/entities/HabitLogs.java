package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

@Entity(tableName = HabitBuilderDatabase.HABIT_LOGS_TABLE)
public class HabitLogs {
    @PrimaryKey(autoGenerate = true)
    private int logId;

    private int habitId;
    private int userId;
    private String date;
    private boolean isCompleted;

    // Constructor (parametrized)
    public HabitLogs(int habitId, int userId, String date, boolean isCompleted) {
        this.habitId = habitId;
        this.userId = userId;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    // Getters
    public int getLogId() { return logId; }
    public int getHabitId() { return habitId; }
    public int getUserId() { return userId; }
    public String getDate() { return date; }
    public boolean isCompleted() { return isCompleted; }

    // Setters
    public void setLogId(int logId) { this.logId = logId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    // Equals and hashCode methods

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HabitLogs habitLogs = (HabitLogs) o;
        return logId == habitLogs.logId && habitId == habitLogs.habitId && userId == habitLogs.userId && isCompleted == habitLogs.isCompleted && Objects.equals(date, habitLogs.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, habitId, userId, date, isCompleted);
    }
}
