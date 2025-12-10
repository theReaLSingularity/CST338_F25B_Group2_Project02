package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

@Entity(tableName = HabitBuilderDatabase.HABITS_TABLE)
public class Habits {
    @PrimaryKey(autoGenerate = true)
    private int habitId;

    private int userId;
    private int categoryId;
    private String title;
    private String startDate;
    private String endDate;
    private boolean isActive;

    // Constructor (parametrized)
    public Habits(int userId, int categoryId, String title, String startDate,
                  String endDate, boolean isActive) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters
    public int getHabitId() { return habitId; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public String getTitle() { return title; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setHabitId(int habitId) { this.habitId = habitId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setTitle(String title) { this.title = title; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { isActive = active; }

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Habits habits = (Habits) o;
        return habitId == habits.habitId && userId == habits.userId &&
                categoryId == habits.categoryId && isActive == habits.isActive &&
                Objects.equals(title, habits.title) && Objects.equals(startDate, habits.startDate)
                && Objects.equals(endDate, habits.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(habitId, userId, categoryId, title, startDate, endDate, isActive);
    }
}
