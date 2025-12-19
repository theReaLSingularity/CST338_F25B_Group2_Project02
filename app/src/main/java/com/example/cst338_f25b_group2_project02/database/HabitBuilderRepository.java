/**
 * <br>
 * Repository layer for the Habit Builder application that provides a clean API
 * for accessing user, habit, habit log, and category data. This class mediates
 * between the Room DAOs and the rest of the app, ensuring that database write
 * operations run on a background thread and exposing read operations via LiveData.
 * <br><br>
 *
 * The repository centralizes data access and makes it easier for Activities and
 * other components to interact with the underlying Room database without needing
 * to know which DAO or query to call directly.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repository that provides an abstraction layer over the Room DAOs. It is
 * responsible for orchestrating database operations and ensuring that writes
 * occur off the main thread via an {@link ExecutorService}.
 */
public class HabitBuilderRepository {

    /**
     * Singleton instance of the repository.
     */
    private static HabitBuilderRepository repository;

    /**
     * Executor used to run database write operations in the background.
     */
    private final ExecutorService executor;

    // DAOs used to interact with the Room database.
    private final CategoriesDAO categoriesDAO;
    private final HabitLogsDAO habitLogsDAO;
    private final HabitsDAO habitsDAO;
    private final UsersDAO usersDAO;

    /**
     * Private constructor taking an {@link Application} and resolving the
     * database from it. Intended for production use.
     *
     * @param application the Application used to obtain the database instance
     */
    private HabitBuilderRepository(Application application) {
        this(HabitBuilderDatabase.getDatabase(application));
    }

    /**
     * Public constructor that accepts a {@link HabitBuilderDatabase}, intended
     * primarily for testing with in-memory database instances.
     *
     * @param db the Room database instance
     */
    public HabitBuilderRepository(HabitBuilderDatabase db) {
        this.categoriesDAO = db.categoriesDAO();
        this.habitLogsDAO = db.habitLogsDAO();
        this.habitsDAO = db.habitsDAO();
        this.usersDAO = db.usersDAO();
        this.executor = HabitBuilderDatabase.databaseWriteExecutor;
    }

    /**
     * Returns the singleton repository instance, creating it if necessary.
     *
     * @param application the Application used to build the underlying database
     * @return the singleton {@link HabitBuilderRepository} instance
     */
    public static synchronized HabitBuilderRepository getRepository(Application application) {
        if (repository == null) {
            repository = new HabitBuilderRepository(application);
        }
        return repository;
    }

    // -------------------------------------------------------------------------
    // Users Methods
    // -------------------------------------------------------------------------

    /**
     * Inserts a new {@link Users} record into the database on a background thread.
     * Used by SignupActivity to create new user accounts.
     *
     * @param user the user to insert
     */
    public void insertUser(Users user) {
        executor.execute(() -> usersDAO.insert(user));
    }

    /**
     * Deletes an existing user from the database on a background thread.
     * Used by ManageActivity when removing user accounts.
     *
     * @param user the user to delete
     */
    public void deleteUser(Users user) {
        executor.execute(() -> usersDAO.delete(user));
    }

    /**
     * Updates the password for a given user ID on a background thread.
     * Used by ManageActivity to reset user passwords.
     *
     * @param userId      the ID of the user whose password will be updated
     * @param newPassword the new password value
     */
    public void updateUserPassword(int userId, String newPassword) {
        executor.execute(() -> usersDAO.updateUserPassword(userId, newPassword));
    }

    /**
     * Retrieves a user by their unique user ID, exposed as LiveData.
     * Used by MainActivity, AccountActivity, EditingActivity, and ManageActivity.
     *
     * @param loggedInUserId the user ID to look up
     * @return LiveData stream of the corresponding {@link Users} object
     */
    public LiveData<Users> getUserByUserId(int loggedInUserId) {
        return usersDAO.getUserByUserID(loggedInUserId);
    }

    /**
     * Retrieves a user by their username, exposed as LiveData.
     * Used by LoginActivity for authentication, SignupActivity to check for
     * duplicates, and ManageActivity to look up users.
     *
     * @param username the username to search for
     * @return LiveData stream containing the matched {@link Users} record,
     *         or {@code null} if no user is found
     */
    public LiveData<Users> getUserByUserName(String username) {
        return usersDAO.getUserByUserName(username);
    }

    // -------------------------------------------------------------------------
    // Habits Methods
    // -------------------------------------------------------------------------

    /**
     * Inserts a new {@link Habits} record and notifies the caller of the
     * newly generated habit ID via a callback. The insert is performed on
     * a background thread.
     *
     * @param habit    the habit to insert
     * @param callback callback that receives the auto-generated habit ID
     */
    public void addNewHabit(Habits habit, OnHabitInsertedListener callback) {
        executor.execute(() -> {
            long newId = habitsDAO.insert(habit);
            callback.onHabitInserted((int) newId);
        });
    }

    /**
     * Callback interface used to notify callers when a new habit has been
     * inserted and its ID is available.
     */
    public interface OnHabitInsertedListener {
        /**
         * Called when a habit insert operation completes.
         *
         * @param habitId the auto-generated ID of the newly inserted habit
         */
        void onHabitInserted(int habitId);
    }

    /**
     * Deletes a habit record on a background thread. Used by EditingActivity
     * when the user decides to remove one of their habits.
     *
     * @param habit the habit to delete
     */
    public void deleteHabit(Habits habit) {
        executor.execute(() -> habitsDAO.delete(habit));
    }

    /**
     * Retrieves all active habits for a given user, exposed as LiveData.
     * Used by EditingActivity to display the user’s current habit list.
     *
     * @param userId the owner user ID
     * @return LiveData list of active {@link Habits} for the specified user
     */
    public LiveData<List<Habits>> getAllActiveHabitsForUser(int userId) {
        return habitsDAO.getAllActiveHabitsForUser(userId);
    }

    /**
     * Retrieves a single habit by its habit ID, exposed as LiveData.
     *
     * @param habitId the ID of the habit to look up
     * @return LiveData stream containing the habit, or {@code null} if not found
     */
    public LiveData<Habits> getHabitByHabitId(int habitId) {
        return habitsDAO.getHabitByHabitId(habitId);
    }

    // -------------------------------------------------------------------------
    // HabitLogs Methods
    // -------------------------------------------------------------------------

    /**
     * Inserts a new habit log entry on a background thread.
     *
     * @param habitLog the {@link HabitLogs} entry to insert
     */
    public void insertNewHabitLog(HabitLogs habitLog) {
        executor.execute(() -> habitLogsDAO.insert(habitLog));
    }

    /**
     * Retrieves a habit log by its log ID, exposed as LiveData.
     *
     * @param logId the ID of the log entry to retrieve
     * @return LiveData stream containing the matching {@link HabitLogs} entry
     */
    public LiveData<HabitLogs> getHabitLogById(int logId) {
        return habitLogsDAO.getHabitLogById(logId);
    }

    /**
     * Updates the completion state of a habit log for a given habit ID
     * and date. The operation is performed on a background thread.
     *
     * @param habitId     the habit ID whose log should be updated
     * @param date        the date of the log entry to update
     * @param isCompleted the new completion state
     */
    public void updateHabitLogCompletedState(int habitId, String date, boolean isCompleted) {
        executor.execute(() -> habitLogsDAO.updateHabitLogCompletedState(habitId, date, isCompleted));
    }

    /**
     * Retrieves all habit logs for a given user on a specific date, exposed
     * as LiveData. Used by MainActivity to display the daily checklist.
     *
     * @param userId the user ID
     * @param date   the date to filter logs by (ISO string format)
     * @return LiveData list of {@link HabitLogs} for the specified user and date
     */
    public LiveData<List<HabitLogs>> getUserHabitsForToday(int userId, String date) {
        return habitLogsDAO.getHabitLogsForToday(userId, date);
    }

    /**
     * Deletes all habit logs associated with a specific habit ID. Used when
     * a habit is removed so that its log history is also cleaned up.
     *
     * @param habitId the ID of the habit whose logs should be deleted
     */
    public void deleteHabitLogsByHabitId(int habitId) {
        executor.execute(() -> habitLogsDAO.deleteHabitLogsByHabitId(habitId));
    }

    // -------------------------------------------------------------------------
    // Categories Methods
    // -------------------------------------------------------------------------

    /**
     * Retrieves the category ID for a given category name (case-insensitive),
     * exposed as LiveData. Used when creating or editing habits to resolve
     * category selection to a foreign key value.
     *
     * @param category the category name to look up
     * @return LiveData stream containing the category ID, or {@code null} if not found
     */
    public LiveData<Integer> getCategoryId(String category) {
        return categoriesDAO.getCategoryId(category);
    }
}
