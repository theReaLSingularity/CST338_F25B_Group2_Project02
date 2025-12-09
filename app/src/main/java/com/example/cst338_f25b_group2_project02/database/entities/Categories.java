package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

@Entity(tableName = HabitBuilderDatabase.CATEGORIES_TABLE)
public class Categories {
    @PrimaryKey
    private int categoryId;

    private String name;

    public Categories(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Getters
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }

    // Setters
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }

    // Equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Categories that = (Categories) o;
        return categoryId == that.categoryId && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name);
    }
}
