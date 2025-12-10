package com.example.cst338_f25b_group2_project02.models;

public class ChecklistItem {
    private String title;
    private boolean completed;

    public ChecklistItem(String title) {
        this.title = title;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
