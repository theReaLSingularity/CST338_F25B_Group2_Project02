package com.example.cst338_f25b_group2_project02.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;

import java.util.Objects;

@Entity(tableName = HabitBuilderDatabase.USERS_TABLE)
public class Users {
    @PrimaryKey(autoGenerate = true)
    private int userId;

    private String username;
    private String password;
    private boolean isAdmin;

    // Constructor (parametrized)
    public Users(String username, String password) {
        this.username = username;
        this.password = password;
        isAdmin = false;
    }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }

    // Equals and hashcode methods
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return userId == users.userId && isAdmin == users.isAdmin &&
                Objects.equals(username, users.username) && Objects.equals(password, users.password);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, username, password, isAdmin); }
}
