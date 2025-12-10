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
    // TODO: Add queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitLogs... habitlogs);

    @Delete
    void delete(HabitLogs habitLog);

    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " ORDER BY logId")
    List<HabitLogs> getAllHabitLogs();

    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE)
    void deleteAll();
}
