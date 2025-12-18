package com.example.cst338_f25b_group2_project02;

import static org.junit.Assert.assertEquals;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import org.junit.Test;

public class UserHabitUpdateTest {

    @Test
    public void habitTitle_updatesCorrectly() {
        Habits habit = new Habits(
                1,
                1,
                "Old Title",
                "2024-01-01",
                "2024-04-01",
                true
        );

        habit.setTitle("New Title");

        assertEquals("New Title", habit.getTitle());
    }
}
