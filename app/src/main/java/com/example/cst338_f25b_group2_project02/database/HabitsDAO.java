/**
 * <br>
 * Data Access Object (DAO) for interacting with the Habits table in the Habit
 * Builder application's Room database. Provides methods for inserting,
 * deleting, and querying habit records, including retrieving only active habits
 * for a specific user.
 * <br><br>
 *
 * This DAO is used throughout the application to support habit creation,
 * editing, deletion, and daily checklist operations.
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

import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import java.util.List;

/**
 * DAO defining operations for the Habits table. Supports inserting new habits,
 * deleting existing habits, retrieving active habits for a user, clearing the
 * table, and querying habits by their unique habit ID.
 */
@Dao
public interface HabitsDAO {

    /**
     * Inserts a habit into the database. If a conflict occurs (such as inserting
     * a habit with the same primary key), the existing record is replaced.
     *
     * @param habits the habit entity to insert
     * @return the auto-generated ID of the newly inserted habit
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Habits habits);

    /**
     * Deletes a specific habit from the database.
     *
     * @param habit the habit to delete
     */
    @Delete
    void delete(Habits habit);

    /**
     * Retrieves all active habits belonging to a specific user, sorted by habit ID.
     * Used primarily in EditingActivity to show habits that the user is actively tracking.
     *
     * @param userId the ID of the user whose active habits should be retrieved
     * @return LiveData list of active {@link Habits} objects for the specified user
     */
    @Query(
            "SELECT * FROM " + HabitBuilderDatabase.HABITS_TABLE +
                    " WHERE userId = :userId AND isActive = true ORDER BY habitId"
    )
    LiveData<List<Habits>> getAllActiveHabitsForUser(int userId);

    /**
     * Deletes all habit records from the Habits table.
     * Intended for development, debugging, or administrative features.
     */
    @Query("DELETE FROM " + HabitBuilderDatabase.HABITS_TABLE)
    void deleteAll();

    /**
     * Retrieves a habit by its unique habit ID.
     *
     * @param habitId the ID of the habit to retrieve
     * @return LiveData object containing the matching {@link Habits} entry,
     *         or {@code null} if no match exists
     */
    @Query(
            "SELECT * FROM " + HabitBuilderDatabase.HABITS_TABLE +
                    " WHERE habitId = :habitId"
    )
    LiveData<Habits> getHabitByHabitId(int habitId);
}
