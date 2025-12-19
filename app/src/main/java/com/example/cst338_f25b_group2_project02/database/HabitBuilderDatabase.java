/**
 * <br>
 * Central Room database class for the Habit Builder application. This database
 * manages all persistent user data including users, categories, habits, and
 * habit logs. It provides access to each DAO and initializes default values
 * when the database is first created.
 * <br><br>
 *
 * The database is implemented as a thread-safe singleton and uses a fixed thread
 * executor for background write operations. On first creation, default users and
 * categories are inserted to support initial application behavior.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room database definition for the Habit Builder application.
 * Contains the schema for all user-related entities and exposes
 * the Data Access Objects (DAOs) used to interact with them.
 */
@Database(
        version = 1,
        entities = {Users.class, Categories.class, Habits.class, HabitLogs.class},
        exportSchema = false
)
public abstract class HabitBuilderDatabase extends RoomDatabase {

    // -------------------------------------------------------------------------
    // Table Names
    // -------------------------------------------------------------------------

    /**
     * Table name for storing user accounts.
     */
    public static final String USERS_TABLE = "users";

    /**
     * Table name for storing habit categories.
     */
    public static final String CATEGORIES_TABLE = "categories";

    /**
     * Table name for storing habit records.
     */
    public static final String HABITS_TABLE = "habits";

    /**
     * Table name for storing daily habit logs.
     */
    public static final String HABIT_LOGS_TABLE = "habitLogs";

    // -------------------------------------------------------------------------
    // Singleton Instance & Configuration
    // -------------------------------------------------------------------------

    /** Name of the database file. */
    private static final String DATABASE_NAME = "HabitBuilderDatabase";

    /** Singleton instance of the database. */
    private static volatile HabitBuilderDatabase INSTANCE;

    /** Executor service for performing background database operations. */
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // -------------------------------------------------------------------------
    // DAO Definitions
    // -------------------------------------------------------------------------

    /**
     * @return Data Access Object for the Users table
     */
    public abstract UsersDAO usersDAO();

    /**
     * @return Data Access Object for the Categories table
     */
    public abstract CategoriesDAO categoriesDAO();

    /**
     * @return Data Access Object for the Habits table
     */
    public abstract HabitsDAO habitsDAO();

    /**
     * @return Data Access Object for the HabitLogs table
     */
    public abstract HabitLogsDAO habitLogsDAO();

    // -------------------------------------------------------------------------
    // Singleton Getter
    // -------------------------------------------------------------------------

    /**
     * Retrieves the singleton database instance, creating it if necessary.
     * Uses double-checked locking to ensure thread safety.
     *
     * @param context application context
     * @return the singleton {@link HabitBuilderDatabase} instance
     */
    static HabitBuilderDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HabitBuilderDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    HabitBuilderDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration() // wipes DB on schema changes (development only)
                            .addCallback(addDefaultValues)    // loads initial data
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Default Data Callback
    // -------------------------------------------------------------------------

    /**
     * Room callback executed when the database is created for the first time.
     * Populates the database with default users and categories to support
     * initial application behavior and simplify development/testing.
     */
    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {

                // Default Users
                UsersDAO usersDAO = INSTANCE.usersDAO();
                usersDAO.deleteAll();  // ensure fresh start
                Users admin = new Users("Admin1", "Admin1", true);
                Users testUser1 = new Users("testuser1", "testuser1", false);
                usersDAO.insert(admin, testUser1);

                // Default Categories
                CategoriesDAO categoriesDAO = INSTANCE.categoriesDAO();
                categoriesDAO.deleteAll();
                categoriesDAO.insert(
                        new Categories("Finances"),
                        new Categories("Health"),
                        new Categories("Education"),
                        new Categories("Accountability")
                );
            });
        }
    };
}
