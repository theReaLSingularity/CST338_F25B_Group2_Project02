package com.example.cst338_f25b_group2_project02.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;

import java.util.List;

@Dao
public interface HabitLogsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitLogs... habitlogs);

    @Delete
    void delete(HabitLogs habitLog);

    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " ORDER BY logId")
    List<HabitLogs> getAllHabitLogs();

    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE)
    void deleteAll();

    @Query("UPDATE " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " SET isCompleted = :isCompleted " +
            "WHERE habitId = :habitId AND date = :date")
    void updateHabitLogCompletedState(int habitId, String date, boolean isCompleted);

    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " WHERE userId = :userId AND date = :date")
    LiveData<List<HabitLogs>> getHabitLogsForToday(int userId, String date);

    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " WHERE habitId = :habitId")
    void deleteHabitLogsByHabitId(int habitId);

    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " WHERE logId = :logId")
    LiveData<HabitLogs> getHabitLogById(int logId);
}
