package com.example.cst338_f25b_group2_project02.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import java.util.List;

@Dao
public interface HabitsDAO {
    // TODO: Add queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Habits... habits);

    @Delete
    void delete(Habits habit);

    @Query("SELECT * FROM " + HabitBuilderDatabase.HABITS_TABLE + " ORDER BY habitId")
    List<Habits> getAllHabits();

    @Query("DELETE FROM " + HabitBuilderDatabase.HABITS_TABLE)
    void deleteAll();
}
