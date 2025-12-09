package com.example.cst338_f25b_group2_project02.database;

import android.app.Application;
import android.util.Log;

import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HabitBuilderRepository {
//    private final CategoriesDAO categoriesDAO;
//    private final HabitLogsDAO habitLogsDAO;
    private final HabitsDAO habitsDAO;
    private final UsersDAO usersDAO;

    private static HabitBuilderRepository repository;

    private HabitBuilderRepository(Application application) {
        HabitBuilderDatabase db = HabitBuilderDatabase.getDatabase(application);

//        this.categoriesDAO = db.categoriesDAO();
//        this.habitLogsDAO = db.habitLogsDAO();
        this.habitsDAO = db.habitsDAO();
        this.usersDAO = db.usersDAO();
    }

    public static HabitBuilderRepository getRepository(Application application) {
        if (repository != null) {
            return repository;
        }

        Future<HabitBuilderRepository> future = HabitBuilderDatabase.databaseWriteExecutor.submit(
                new Callable<HabitBuilderRepository>() {
                    @Override
                    public HabitBuilderRepository call() throws Exception {
                        return new HabitBuilderRepository(application);
                    }
                }
        );

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.d("ERROR","Problem getting repository, thread error.");
        }
        return null;
    }

    public void insertUser(Users... user) {
        HabitBuilderDatabase.databaseWriteExecutor.execute(() -> {
            usersDAO.insert(user);
        });
    }

    // FIXME: Temporarily using to trigger DB inspector; replace
    public ArrayList<Users> getAllLogs() {
        Future<ArrayList<Users>> future = HabitBuilderDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<Users>>() {
                    @Override
                    public ArrayList<Users> call() throws Exception {
                        return (ArrayList<Users>) usersDAO.getAllUsers();
                    }
                });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Habits> getAllHabits() {
        Future<ArrayList<Habits>> future = HabitBuilderDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<Habits>>() {
                    @Override
                    public ArrayList<Habits> call() throws Exception {
                        return (ArrayList<Habits>) habitsDAO.getAllHabits();
                    }
                });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
