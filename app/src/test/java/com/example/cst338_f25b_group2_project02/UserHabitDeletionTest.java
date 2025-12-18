package com.example.cst338_f25b_group2_project02;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import org.junit.Test;
// new updates more
public class UserHabitDeletionTest {

    @Test
    public void habitSelectionForDeletion_togglesCorrectly() {
        Habits habit = new Habits(
                2,
                3,
                "Drink Water",
                "2024-01-01",
                "2024-04-01",
                true
        );

        // default should be false
        assertFalse(habit.isSelectedForDeletion());

        habit.setSelectedForDeletion(true);

        assertTrue(habit.isSelectedForDeletion());
    }
}
