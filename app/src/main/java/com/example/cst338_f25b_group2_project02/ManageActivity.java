package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityManageBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

public class ManageActivity extends AuthenticatedActivity {

    ActivityManageBinding binding;
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

        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpManageActivityNavigation();

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

    private void setUpManageActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewManage.setSelectedItemId(R.id.manage);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewManage.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();

            // TODO: Implement intent factories with startActivity(intent) calls
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
                startActivity(new Intent(getApplicationContext(), EditingActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.manage) {
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                return session.isAdmin();
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
                binding.bottomNavigationViewManage.getMenu().findItem(R.id.account).setTitle(user.getUsername());
            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewManage.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    // TODO: Create intent factory
}

// NOTE: ManageActivity is available to Admins ONLY
//  The Layout will consist of three EditTexts, one for a username, one for a password,
//  and another for a password confirmation.
//  There will be two Buttons, one for resetting the users password and another to delete
//  the user.
//  If the reset password is pressed, the username must exist in the database, and the
//  passwords must match.
//  If delete account is pressed, the user will be deleted using the repository.deleteUser(user)
//  method.
//  The Users object will be fetched from the DB using the repository.getUserByUserName(username)
//  method and the Users entity setPassword(password) method will be used to reset the password.