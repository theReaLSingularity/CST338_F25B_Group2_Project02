/**
 * <br>
 * Data Access Object (DAO) for interacting with the HabitLogs table in the Room
 * database. Habit logs represent whether a habit was completed on a specific date
 * by a specific user, enabling daily checklists and progress-tracking features.
 * <br><br>
 *
 * This DAO provides CRUD operations for habit logs, including insertion, deletion,
 * updating completion status, and retrieving logs for a given user and date.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;

import java.util.List;

/**
 * DAO interface defining methods for interacting with the HabitLogs table.
 * Supports inserting, updating, deleting, and querying daily habit completion
 * records for users.
 */
@Dao
public interface HabitLogsDAO {

    /**
     * Inserts one or more {@link HabitLogs} records into the database.
     * If a conflict occurs (e.g., same habitId + date), the existing record
     * is replaced.
     *
     * @param habitlogs one or more habit log entries to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitLogs... habitlogs);

    /**
     * Deletes a specific habit log entry from the database.
     *
     * @param habitLog the log entry to delete
     */
    @Delete
    void delete(HabitLogs habitLog);

    /**
     * Retrieves all habit log entries in the database, ordered by their log ID.
     * Intended for internal use or debugging.
     *
     * @return a list of all {@link HabitLogs} records
     */
    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " ORDER BY logId")
    List<HabitLogs> getAllHabitLogs();

    /**
     * Deletes all habit logs from the table. Used primarily for testing.
     */
    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE)
    void deleteAll();

    /**
     * Updates the completion status of a habit log for a specific habit ID and date.
     * Used when a user toggles a habit's completion checkbox.
     *
     * @param habitId     the habit ID to match
     * @param date        the date string (ISO format)
     * @param isCompleted the new completion state
     */
    @Query(
            "UPDATE " + HabitBuilderDatabase.HABIT_LOGS_TABLE +
                    " SET isCompleted = :isCompleted " +
                    "WHERE habitId = :habitId AND date = :date"
    )
    void updateHabitLogCompletedState(int habitId, String date, boolean isCompleted);

    /**
     * Retrieves all habit log entries for a specific user on a given date.
     * Used by MainActivity to populate the daily checklist.
     *
     * @param userId the user ID
     * @param date   the date to filter logs by
     * @return LiveData list of matching {@link HabitLogs} records
     */
    @Query(
            "SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE +
                    " WHERE userId = :userId AND date = :date"
    )
    LiveData<List<HabitLogs>> getHabitLogsForToday(int userId, String date);

    /**
     * Deletes all habit logs associated with a specific habit. Used when a habit
     * is deleted to also remove its historical logs.
     *
     * @param habitId the ID of the habit whose logs should be deleted
     */
    @Query("DELETE FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " WHERE habitId = :habitId")
    void deleteHabitLogsByHabitId(int habitId);

    /**
     * Retrieves a single habit log entry by its unique log ID.
     *
     * @param logId the log ID
     * @return LiveData stream containing the matching {@link HabitLogs} entry
     */
    @Query("SELECT * FROM " + HabitBuilderDatabase.HABIT_LOGS_TABLE + " WHERE logId = :logId")
    LiveData<HabitLogs> getHabitLogById(int logId);
}
