package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityEditingBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

public class EditingActivity extends AuthenticatedActivity {

    ActivityEditingBinding binding;
    HabitBuilderRepository repository;

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

        // TODO: Initialize activity methods here

    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    // TODO: Define activity methods here

    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpEditingActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewEditing.setSelectedItemId(R.id.edit);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewEditing.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();

            // TODO: Implement intent factories with startActivity(intent) calls
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
                return true;
            }
            else if (menuItemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.manage) {
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                if (!session.isAdmin()) {
                    return false;
                }
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
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

    // TODO: Create intent factory
}

// NOTE: The EditingActivity is where the user adds and deletes habits
//  The Layout will consist of one EditText for entering a new habit to be added.
//  There will be one dropdown Spinner to select a category to attach to the new habit.
//  There will be a checklist sorted by category with checkboxes to select habits.
//  There will be a Button to delete the selected habits.
//  On click of the Add button, the new habit and selected category (both required), will be
//  added to the habits table, and a new entry in the habits log will be made.
//  The fields will be cleared, and a Toast will inform the user that the habit was added.
//  In the checklist, selected habits will be marked red and crossed-out.
//  On press of the Delete button, a confirmation dialog will open, stating how many habits
//  will be deleted, and if selected Delete, will be deleted from the habits and habitslog tables.
//  Toast will confirm items were deleted.
//  On longClick of the habits, a fragment will open to edit the habit name, category, and to reset
//  the start date to today.

// TODO: need to perform check to mark inactive habits that are past 90 days

// TODO: need to standardize document comments, document headers and place throughout
