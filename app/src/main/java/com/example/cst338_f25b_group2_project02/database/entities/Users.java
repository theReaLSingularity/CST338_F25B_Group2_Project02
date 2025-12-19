/**
 * <br>
 * Represents an application user within the Habit Builder system. Users have a unique
 * auto-generated ID, a username, a password, and an admin privilege flag that determines
 * whether they have access to management features in the application.
 * <br><br>
 *
 * This entity is persisted in the Room database table defined by
 * {@link HabitBuilderDatabase#USERS_TABLE}. Each user may own habits, categories,
 * and habit logs stored across other tables.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

/**
 * Entity class representing a user account in the Habit Builder application.
 * Users can be regular users or administrators.
 */
@Entity(tableName = HabitBuilderDatabase.USERS_TABLE)
public class Users {

    /**
     * Auto-generated unique identifier for the user.
     */
    @PrimaryKey(autoGenerate = true)
    private int userId;

    /**
     * The username chosen by the user. Must be unique within the application.
     */
    private String username;

    /**
     * The user's password in plain text.
     * <b>Note:</b> For academic purposes only; real applications must use hashing.
     */
    private String password;

    /**
     * Flag indicating whether this user has administrative privileges.
     */
    private boolean isAdmin;

    /**
     * Constructs a new user with the specified username, password, and admin status.
     * The {@code userId} will be auto-assigned by Room upon insertion.
     *
     * @param username the user's login name
     * @param password the user's password
     * @param isAdmin  whether the user has admin privileges
     */
    public Users(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // ------------------- Setters -------------------

    /**
     * Sets the user's unique ID. Normally assigned internally by Room.
     *
     * @param userId the ID to assign
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Updates the user's username.
     *
     * @param username the new username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Updates the user's password.
     *
     * @param password the new password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Updates the user's admin privilege state.
     *
     * @param admin whether the user should be an admin
     */
    public void setAdmin(boolean admin) { isAdmin = admin; }

    // ------------------- Getters -------------------

    /**
     * @return the auto-generated ID of this user
     */
    public int getUserId() { return userId; }

    /**
     * @return the user's username
     */
    public String getUsername() { return username; }

    /**
     * @return the user's password (stored in plain text for assignment use only)
     */
    public String getPassword() { return password; }

    /**
     * @return {@code true} if the user has admin privileges, {@code false} otherwise
     */
    public boolean isAdmin() { return isAdmin; }

    // ------------------- Equality -------------------

    /**
     * Compares this user to another object for equality. Two users are considered equal
     * if all fields (ID, username, password, admin flag) match.
     *
     * @param o the object to compare with
     * @return {@code true} if both objects represent the same user
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return userId == users.userId &&
                isAdmin == users.isAdmin &&
                Objects.equals(username, users.username) &&
                Objects.equals(password, users.password);
    }

    /**
     * Generates a hash code based on all core user fields.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, isAdmin);
    }
}
