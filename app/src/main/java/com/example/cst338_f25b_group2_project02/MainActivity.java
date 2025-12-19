/**
 * <br>
 * MainActivity is the primary home screen of the Habit Builder application.
 * It displays the user's daily habit checklist, allowing them to mark habits
 * as completed for the current day. The checklist is built by joining:
 * <ul>
 *     <li>Active {@link com.example.cst338_f25b_group2_project02.database.entities.Habits} for the user</li>
 *     <li>Today's {@link com.example.cst338_f25b_group2_project02.database.entities.HabitLogs}</li>
 * </ul>
 * When a checkbox is toggled, the corresponding habit log is updated in the
 * Room database via {@link com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository}.
 * <br><br>
 *
 * MainActivity extends {@link com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity}
 * to ensure that only authenticated users can access this screen. It also
 * configures the bottom navigation bar to navigate to the Edit, Account,
 * and Manage activities (with Manage restricted to admin users).
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.adapters.ChecklistAdapter;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.models.HabitLogChecklistItem;
import com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Home screen of the app that shows today's checklist of habits for the
 * logged-in user. It wires up the RecyclerView, observes LiveData from
 * the repository, and configures bottom navigation.
 */
public class MainActivity extends AuthenticatedActivity {

    ActivityMainBinding binding;
    HabitBuilderRepository repository;

    // RecyclerView and adapter for displaying today's checklist
    ChecklistAdapter adapter;
    RecyclerView recyclerView;

    // *************************************
    //      User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;
    // *************************************

    /**
     * Called when the activity is created. Initializes bindings, repository,
     * session state, navigation, and sets up the daily checklist RecyclerView by
     * observing both active habits and today's habit logs.
     *
     * @param savedInstanceState previously saved state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *********************************
        //         Initialization
        // *********************************

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpMainActivityNavigation();

        // *********************************
        //           Activity
        // *********************************

        // Adding the main recycler view
        recyclerView = findViewById(R.id.recyclerDailyChecklist);

        // When a checkbox is toggled, update the corresponding HabitLog record
        adapter = new ChecklistAdapter((item, isChecked) -> {
            repository.updateHabitLogCompletedState(
                    item.getHabitId(),
                    item.getDate(),
                    isChecked
            );
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Cache of habitId -> habitTitle for display
        Map<Integer, String> habitTitles = new HashMap<>();

        // Observe all active habits to populate the title map
        repository.getAllActiveHabitsForUser(loggedInUserId).observe(this, habits -> {
            habitTitles.clear();
            if (habits != null) {
                for (Habits h : habits) {
                    habitTitles.put(h.getHabitId(), h.getTitle());
                }
            }
        });

        // Observe today's habit logs and build checklist items from them
        repository.getUserHabitsForToday(loggedInUserId, LocalDate.now().toString())
                .observe(this, habitLogs -> {
                    if (habitLogs != null) {
                        List<HabitLogChecklistItem> checklistItems = new ArrayList<>();
                        for (HabitLogs log : habitLogs) {
                            String title = habitTitles.getOrDefault(log.getHabitId(), "Habit");
                            checklistItems.add(new HabitLogChecklistItem(title, log));
                        }
                        adapter.setHabitLogs(checklistItems);
                    }
                });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************



    // *************************************
    //       Initialization Methods
    // *************************************

    /**
     * Configures the bottom navigation bar for MainActivity, marks the Home
     * item as selected, and defines navigation behavior to the Edit, Account,
     * and Manage activities. The Manage item is only accessible to admin users
     * as determined by {@link SessionManager}.
     */
    private void setUpMainActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewHome.setSelectedItemId(R.id.home);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewHome.setOnItemSelectedListener(item -> {
            int menuItemId = item.getItemId();

            if (menuItemId == R.id.home) {
                return true;
            } else if (menuItemId == R.id.edit) {
                startActivity(EditingActivity.editingActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            } else if (menuItemId == R.id.account) {
                startActivity(AccountActivity.accountActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            } else if (menuItemId == R.id.manage) {
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                if (!session.isAdmin()) {
                    return false;
                }
                startActivity(ManageActivity.manageActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            }

            return false;
        });
    }

    /**
     * Observes the currently logged-in user based on the user ID stored in the
     * session. When user data is loaded, updates the admin flag, configures the
     * Manage menu visibility, and sets the Account tab title to the username.
     */
    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewHome.getMenu()
                        .findItem(R.id.account)
                        .setTitle(user.getUsername());
            }
        });
    }

    /**
     * Controls whether the admin-only Manage item is visible and enabled in
     * the bottom navigation.
     *
     * @param isVisible {@code true} to show and enable the Manage item,
     *                  {@code false} to hide and disable it
     */
    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewHome.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    /**
     * Intent factory method for launching {@link MainActivity}.
     *
     * @param context the Context from which MainActivity is being started
     * @return an {@link Intent} configured for MainActivity
     */
    public static Intent mainActivityIntentFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
