package com.example.cst338_f25b_group2_project02.models;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;


public class HabitLogChecklistItem {
    HabitLogs habitLog;
    private String title;
    private boolean completed;

    public HabitLogChecklistItem(String title, HabitLogs habitLog) {
        this.habitLog = habitLog;
        this.title = title;
        this.completed = habitLog.isCompleted();


    }

    public String getHabitTitle() { return title; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public HabitLogs getHabitLog() { return habitLog; }
    public int getHabitId() { return habitLog.getHabitId(); }
    public String getDate() { return habitLog.getDate(); }
}
