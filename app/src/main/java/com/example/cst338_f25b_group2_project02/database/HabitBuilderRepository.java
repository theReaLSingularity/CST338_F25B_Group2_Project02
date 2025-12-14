package com.example.cst338_f25b_group2_project02.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class HabitBuilderRepository {
    private static HabitBuilderRepository repository;
    private final ExecutorService executor;

    private final CategoriesDAO categoriesDAO;
    private final HabitLogsDAO habitLogsDAO;
    private final HabitsDAO habitsDAO;
    private final UsersDAO usersDAO;

    private HabitBuilderRepository(Application application) {
        HabitBuilderDatabase db = HabitBuilderDatabase.getDatabase(application);

        this.categoriesDAO = db.categoriesDAO();
        this.habitLogsDAO = db.habitLogsDAO();
        this.habitsDAO = db.habitsDAO();
        this.usersDAO = db.usersDAO();

        executor = HabitBuilderDatabase.databaseWriteExecutor;
    }

    public static synchronized HabitBuilderRepository getRepository(Application application) {
        if (repository == null) {
            repository = new HabitBuilderRepository(application);
        }

        return repository;
    }

    // Writes (using executor)
    public void insertUser(Users user) {
        executor.execute(()-> usersDAO.insert(user));
    }

    public LiveData<Users> getUserByUserName(String username) {
        return usersDAO.getUserByUserName(username);
    }

    // Reads (using LiveData)
    public LiveData<Users> getUserByUserId(int loggedInUserId) {
        return usersDAO.getUserByUserID(loggedInUserId);
    }
}
