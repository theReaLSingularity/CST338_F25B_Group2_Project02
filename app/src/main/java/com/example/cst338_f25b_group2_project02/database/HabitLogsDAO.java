package com.example.cst338_f25b_group2_project02.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;

import java.util.List;

@Dao
public interface HabitLogsDAO {

    // Insert one or more habit log entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitLogs... habitLogs);

    // Delete a specific habit log
    @Delete
    void delete(HabitLogs habitLog);

    // Get all habit logs (used for debugging / admin / metrics)
    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " ORDER BY logId")
    List<HabitLogs> getAllHabitLogs();

    // NEW: Get all logs for a specific habit
    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE +
            " WHERE habitId = :habitId")
    List<HabitLogs> getLogsForHabit(int habitId);

    // NEW: Get logs within a date range (used for metrics)
    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE +
            " WHERE date BETWEEN :startDate AND :endDate")
    List<HabitLogs> getLogsBetweenDates(long startDate, long endDate);

    // Delete all logs (used for testing or reset)
    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE)
    void deleteAll();
}
