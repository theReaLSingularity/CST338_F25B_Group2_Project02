package com.example.cst338_f25b_group2_project02;
import android.util.Log;

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
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityEditingBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class EditingActivity extends AuthenticatedActivity implements AdapterView.OnItemSelectedListener {

    ActivityEditingBinding binding;
    HabitBuilderRepository repository;

    // Adding recyclerView and adapter declarations
    EditingAdapter adapter;
    RecyclerView recyclerView;


    // Adding spinner declarations
    private Spinner categorySpinner;
    private Integer selectedCategoryId = null;

    private LiveData<Integer> categoryIdLiveData;



    // *************************************
    //      User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;
    // *************************************

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
        binding.addNewHabitEditingButton.setOnClickListener(v -> {
            Log.d("EDITING", "Button clicked. selectedCategoryId = " + selectedCategoryId);
            Log.d("EDITING", "Habit text = " + binding.newHabitEditingEditText.getText().toString());

            if (binding.newHabitEditingEditText.getText().toString().trim().isEmpty()
                    || selectedCategoryId == null) {
                Toast.makeText(
                        EditingActivity.this,
                        "Enter a habit and select a category",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            addNewHabit();
        });


        // Adding the editing recycler view
        recyclerView = findViewById(R.id.habitEditingRecyclerView);

        adapter = new EditingAdapter( habit -> {
            habit.setSelectedForDeletion(true);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        repository.getAllActiveHabitsForUser(loggedInUserId).observe(this, habitsList -> {
            if (habitsList != null) {
                adapter.setHabits(habitsList);
            }
        });

        // Delete button listener
        binding.deleteHabitsEditingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Habits> current = adapter.getHabits();
                for (Habits h : current) {
                    if (h.isSelectedForDeletion()) {
                        repository.deleteHabit(h);
                    }
                }
            }
        });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    // Creates the categories Spinner dropdown menu
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

    // Gets category id from categories dropdown selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Log.d("EDITING", "Spinner position selected: " + pos);

        if (pos > 0) {
            String categoryName = parent.getItemAtPosition(pos).toString();
            Log.d("EDITING", "Category selected: " + categoryName);

            if (categoryIdLiveData != null) {
                categoryIdLiveData.removeObservers(this);
            }

            categoryIdLiveData = repository.getCategoryId(categoryName);
            categoryIdLiveData.observe(this, cat -> {
                Log.d("EDITING", "Category ID from DB: " + cat);

                selectedCategoryId = cat;
            });
        } else {
            Log.d("EDITING", "No category selected");
            selectedCategoryId = null;
        }
    }

    // Displays a Toast to select a category when nothing selected in categories dropdown

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedCategoryId = null;
    }


    // Adds a new habit to the database 'habits' table
    private void addNewHabit() {
        if (user == null || selectedCategoryId == null) {
            Toast.makeText(this, "Invalid habit data", Toast.LENGTH_SHORT).show();
            return;
        }

        Habits newHabit = new Habits(
                user.getUserId(),
                selectedCategoryId,
                binding.newHabitEditingEditText.getText().toString().trim(),
                LocalDate.now().toString(),
                LocalDate.now().plusDays(90).toString(),
                true
        );

        repository.addNewHabit(newHabit);
    }



// *************************************
    //       Initialization Methods
    // *************************************

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

    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewEditing.getMenu().findItem(R.id.account).setTitle(user.getUsername());
            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewEditing.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    public static Intent editingActivityIntentFactory(Context context) {
        return new Intent(context, EditingActivity.class);
    }
}
