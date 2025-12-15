package com.example.cst338_f25b_group2_project02.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.List;

@Dao
public interface UsersDAO {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Users... user);

    @Delete
    void delete(Users user);

    @Query("SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE + " ORDER BY username")
    List<Users> getAllUsers();

    @Query("DELETE FROM " + HabitBuilderDatabase.USERS_TABLE)
    void deleteAll();

    @Query("SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE + " WHERE username = :username LIMIT 1")
    LiveData<Users> getUserByUserName(String username);

    @Query("SELECT * FROM " + HabitBuilderDatabase.USERS_TABLE + " WHERE userId = :loggedInUserId LIMIT 1")
    LiveData<Users> getUserByUserID(int loggedInUserId);
}
