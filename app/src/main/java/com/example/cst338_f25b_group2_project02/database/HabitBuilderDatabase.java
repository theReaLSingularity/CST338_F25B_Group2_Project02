package com.example.cst338_f25b_group2_project02.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cst338_f25b_group2_project02.database.entities.Categories;
import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(version = 2, entities = {Users.class, Categories.class, Habits.class, HabitLogs.class},
          exportSchema = false)
public abstract class HabitBuilderDatabase extends RoomDatabase {

    public static final String USERS_TABLE = "users";
    public static final String CATEGORIES_TABLE = "categories";
    public static final String HABITS_TABLE = "habits";
    public static final String HABIT_LOGS_TABLE = "habitLogs";

    private static final String DATABASE_NAME = "HabitBuilderDatabase";
    private static volatile HabitBuilderDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HabitBuilderDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HabitBuilderDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    HabitBuilderDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Adding users for development testing and troubleshooting
                UsersDAO usersDAO = INSTANCE.usersDAO();
                usersDAO.deleteAll();
                Users admin = new Users("Admin1", "Admin1");
                admin.setAdmin(true);
                usersDAO.insert(admin);
                Users testUser1 = new Users("testuser1", "testuser1");
                usersDAO.insert(testUser1);

                HabitsDAO habitsDAO = INSTANCE.habitsDAO();
                habitsDAO.deleteAll();
                Habits habitOne = new Habits(1, 1, 2, "Wake up by 9 am",
                        LocalDate.now().toString(), LocalDate.now().plusDays(90).toString(), true);
                habitsDAO.insert(habitOne);
            });
        }
    };

    public abstract UsersDAO usersDAO();
    public abstract CategoriesDAO categoriesDAO();
    public abstract HabitsDAO habitsDAO();
    public abstract HabitLogsDAO habitLogsDAO();

}
