/**
 * <br>
 * Data Access Object (DAO) for interacting with the Users table in the Habit
 * Builder application's Room database. Provides methods for inserting, deleting,
 * updating, and querying user records.
 * <br><br>
 *
 * This DAO is used across application features, including authentication,
 * account management, admin functionality, and retrieving user details for UI
 * screens such as MainActivity, AccountActivity, and ManageActivity.
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

import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.List;

/**
 * DAO defining database operations for the Users table. Supports user
 * creation, deletion, credential updates, and lookups by username or ID.
 */
@Dao
public interface UsersDAO {

    /**
     * Inserts one or more {@link Users} into the database. If a user with the
     * same primary key already exists, it will be replaced.
     *
     * @param user one or more Users to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Users... user);

    /**
     * Deletes the specified user from the database.
     *
     * @param user the user to delete
     */
    @Delete
    void delete(Users user);

    /**
     * Retrieves all users in the system, sorted alphabetically by username.
     *
     * @return a list of all {@link Users} records
     */
    @Query("SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE + " ORDER BY username")
    List<Users> getAllUsers();

    /**
     * Deletes all users from the Users table.
     * Intended for admin operations or testing.
     */
    @Query("DELETE FROM " + HabitBuilderDatabase.USERS_TABLE)
    void deleteAll();

    /**
     * Retrieves a user by their username.
     * Used primarily by LoginActivity during authentication and by SignupActivity
     * when checking for duplicate accounts.
     *
     * @param username the username to search for
     * @return a {@link LiveData} stream containing the user record if found
     */
    @Query(
            "SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE +
                    " WHERE username = :username LIMIT 1"
    )
    LiveData<Users> getUserByUserName(String username);

    /**
     * Retrieves a user by their unique user ID.
     * Used by MainActivity, AccountActivity, EditingActivity, and ManageActivity
     * to load user profile information or validate permissions.
     *
     * @param loggedInUserId the user's ID
     * @return a {@link LiveData} stream containing the user record
     */
    @Query(
            "SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE +
                    " WHERE userId = :loggedInUserId LIMIT 1"
    )
    LiveData<Users> getUserByUserID(int loggedInUserId);

    /**
     * Updates the password of a user identified by their unique user ID.
     * Used by ManageActivity for administrative password resets.
     *
     * @param userId      the ID of the user whose password should be updated
     * @param newPassword the new password to set
     */
    @Query(
            "UPDATE " + HabitBuilderDatabase.USERS_TABLE +
                    " SET password = :newPassword WHERE userId = :userId"
    )
    void updateUserPassword(int userId, String newPassword);
}
