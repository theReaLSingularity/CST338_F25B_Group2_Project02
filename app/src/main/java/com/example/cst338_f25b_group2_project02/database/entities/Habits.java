package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

@Entity(tableName = HabitBuilderDatabase.HABITS_TABLE)
public class Habits {
    // TODO: Add table field instance attributes and annotations
    @PrimaryKey
    // FIXME: Change to proper column name; was done for quick testing
    int number;

    // TODO: Generate getters, setters, equals, hashcode, and constructor
}
