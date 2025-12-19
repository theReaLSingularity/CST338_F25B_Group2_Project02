/**
 * <br>
 * EditingActivity allows the logged-in user to manage their personal habits
 * within the Habit Builder application. From this screen, users can:
 * <ul>
 *     <li>Create new habits and assign them to a category</li>
 *     <li>View their active habits in a RecyclerView</li>
 *     <li>Mark habits for deletion and remove them (along with their logs)</li>
 * </ul>
 * The activity uses a Spinner for category selection, a RecyclerView for the
 * habit list, and communicates with the Room database via
 * {@link com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository}.
 * <br><br>
 *
 * EditingActivity extends {@link com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity}
 * to enforce that only authenticated users can access this screen.
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.adapters.EditingAdapter;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityEditingBinding;
import com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class EditingActivity extends AuthenticatedActivity implements AdapterView.OnItemSelectedListener {

    ActivityEditingBinding binding;
    HabitBuilderRepository repository;

    // RecyclerView and adapter for displaying and selecting habits
    EditingAdapter adapter;
    RecyclerView recyclerView;

    // Spinner for habit category selection
    private Spinner categorySpinner;
    private int selectedCategoryId = -1;

    // *************************************
    //      User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;
    // *************************************

    /**
     * Called when the activity is first created. Initializes view binding,
     * repository, user session, and sets up the bottom navigation, category
     * dropdown, habit list RecyclerView, and button click listeners for adding
     * and deleting habits.
     *
     * @param savedInstanceState previously saved instance state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *********************************
        //         Initialization
        // *********************************

        binding = ActivityEditingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpEditingActivityNavigation();

        // *********************************
        //           Activity
        // *********************************

        // Creates the categories dropdown
        createCategoriesDropdown();

        // Add new habit button listener
        binding.addNewHabitEditingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.newHabitEditingEditText.toString().isEmpty() || selectedCategoryId == -1) {
                    Toast.makeText(getApplicationContext(), "Enter a habit and select a category",Toast.LENGTH_SHORT).show();
                    return;
                }
                addNewHabit();
            }
        });

        // Configure the RecyclerView for editing habits
        recyclerView = findViewById(R.id.habitEditingRecyclerView);

        adapter = new EditingAdapter(habit -> {
            habit.setSelectedForDeletion(true);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Observe and display all active habits for the logged-in user
        repository.getAllActiveHabitsForUser(loggedInUserId).observe(this, habitsList -> {
            if (habitsList != null) {
                adapter.setHabits(habitsList);
            }
        });

        // Delete button: removes all habits marked for deletion and their logs
        binding.deleteHabitsEditingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Habits> current = adapter.getHabits();
                for (Habits h : current) {
                    if (h.isSelectedForDeletion()) {
                        repository.deleteHabit(h);
                        repository.deleteHabitLogsByHabitId(h.getHabitId());
                    }
                }
            }
        });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    /**
     * Initializes the category selection Spinner, assigning an ArrayAdapter
     * from a string-array resource and registering this activity as the
     * {@link AdapterView.OnItemSelectedListener}.
     */
    private void createCategoriesDropdown() {
        categorySpinner = findViewById(R.id.categorySelectEditingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(this);
    }

    /**
     * Called when a category is selected in the Spinner. For valid positions,
     * looks up the corresponding category ID from the repository and stores
     * it in {@code selectedCategoryId}. If the "placeholder" option is chosen,
     * resets the selection to -1.
     *
     * @param parent the AdapterView where the selection happened
     * @param view   the view within the AdapterView that was clicked
     * @param pos    the position of the view in the adapter
     * @param id     the row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos > 0) {
            LiveData<Integer> catId = repository.getCategoryId(parent.getItemAtPosition(pos).toString());
            catId.observe(this, cat -> {
                selectedCategoryId = cat;
            });
        }
        else {
            selectedCategoryId = -1;
        }
    }

    /**
     * Called when no item is selected in the Spinner. Displays a Toast
     * prompting the user to choose a category.
     *
     * @param parent the AdapterView that now contains no selected item
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this,"Please select a category.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates and inserts a new habit for the logged-in user, using the
     * currently selected category and the habit title entered in the UI.
     * After inserting the habit via the repository, automatically creates
     * 90 days of {@link HabitLogs} entries for this new habit.
     */
    private void addNewHabit() {
        Habits newHabit = new Habits(
                user.getUserId(),
                selectedCategoryId,
                binding.newHabitEditingEditText.getText().toString(),
                LocalDate.now().toString(),
                LocalDate.now().plusDays(90).toString(),
                true
        );

        repository.addNewHabit(newHabit, newHabitId -> {
            for (int day = 0; day < 90; day++) {
                HabitLogs habitLog = new HabitLogs(
                        newHabitId,
                        loggedInUserId,
                        LocalDate.now().plusDays(day).toString(),
                        false
                );
                repository.insertNewHabitLog(habitLog);
            }
        });
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    /**
     * Configures the bottom navigation bar for EditingActivity, marks the Edit
     * item as selected, and defines navigation to other activities (Home,
     * Account, Manage). The Manage item is only accessible to admin users.
     */
    private void setUpEditingActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewEditing.setSelectedItemId(R.id.edit);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewEditing.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();

            if (menuItemId == R.id.home) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
                return true;
            }
            else if (menuItemId == R.id.account) {
                startActivity(AccountActivity.accountActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            }
            else if (menuItemId == R.id.manage) {
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
     * Observes the currently logged-in user from the repository. When the user
     * data is loaded, updates the admin flag, configures Manage item visibility,
     * and sets the Account tab title to the user's username.
     */
    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewEditing.getMenu()
                        .findItem(R.id.account)
                        .setTitle(user.getUsername());
            }
        });
    }

    /**
     * Shows or hides the admin-only Manage menu item based on the provided
     * visibility flag.
     *
     * @param isVisible {@code true} to enable and show the Manage item,
     *                  {@code false} to hide and disable it
     */
    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewEditing.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    /**
     * Intent factory for launching {@link EditingActivity}.
     *
     * @param context the context from which the activity is being started
     * @return an {@link Intent} configured for EditingActivity
     */
    public static Intent editingActivityIntentFactory(Context context) {
        return new Intent(context, EditingActivity.class);
    }
}
