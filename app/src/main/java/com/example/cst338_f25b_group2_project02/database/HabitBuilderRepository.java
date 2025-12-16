package com.example.cst338_f25b_group2_project02.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.ArrayList;
import java.util.List;
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

    //     **************************************
    //     *** ***      Users Methods     *** ***
    //     **************************************

    // For: SignupActivity (to add users)
    public void insertUser(Users user) {
        executor.execute(()-> usersDAO.insert(user));
    }

    // For: ManageActivity (to delete users)
    public void deleteUser(Users user) {
        executor.execute(()-> usersDAO.delete(user));
    }

    // For: ManageActivity (to update user password)
    public void updateUserPassword(int userId, String newPassword) {
        executor.execute(()-> { usersDAO.updateUserPassword(userId, newPassword); });
    }

    // For: MainActivity / AccountActivity / EditingActivity / ManageActivity (to update user and isAdmin)
    public LiveData<Users> getUserByUserId(int loggedInUserId) {
        return usersDAO.getUserByUserID(loggedInUserId);
    }

    // For: LoginActivity (to authenticate users)
    // For: SignupActivity (to verify username doesn't already exist)
    // For: ManageActivity (to fetch users to reset password or delete)
    public LiveData<Users> getUserByUserName(String username) {
        return usersDAO.getUserByUserName(username);
    }

    //     **************************************
    //     *** ***     Habits Methods     *** ***
    //     **************************************

    // For: EditingActivity (to insert a new habit)
    public void addNewHabit(Habits habit) {
        executor.execute(() -> habitsDAO.insert(habit));
    }

    // For: EditingActivity (to delete user habits)
    public void deleteHabit(Habits habit) {
        executor.execute(()-> habitsDAO.delete(habit));
    }

    // For: EditingActivity (to display active user habits)
    public LiveData<List<Habits>> getAllActiveHabitsForUser(int userId) {
        return habitsDAO.getAllActiveHabitsForUser(userId);
    }


    //     **************************************
    //     *** ***    HabitLogs Methods   *** ***
    //     **************************************


    //     **************************************
    //     *** ***   Categories Methods   *** ***
    //     **************************************

    public LiveData<Integer> getCategoryId(String category) {
        return categoriesDAO.getCategoryId(category);
    }

}
