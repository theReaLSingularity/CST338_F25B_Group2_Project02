package com.example.cst338_f25b_group2_project02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityAccountBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

public class AccountActivity extends AuthenticatedActivity {

    ActivityAccountBinding binding;
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

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpAccountActivityNavigation();

        // *********************************
        //           Activity
        // *********************************

        // TODO: Initialize activity methods here

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    // TODO: Define activity methods here

    // Logout functionality
    private void showLogoutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AccountActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();
        alertBuilder.setMessage("Logout?");
        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertBuilder.create().show();
    }
    private void logout() {
        SessionManager session = SessionManager.getInstance(getApplicationContext());
        session.logout();

        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
        finish();
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpAccountActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewAccount.setSelectedItemId(R.id.account);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewAccount.setOnItemSelectedListener( item -> {
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
                binding.bottomNavigationViewAccount.getMenu().findItem(R.id.account).setTitle(user.getUsername());

            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewAccount.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    // TODO: Create intent factory
}

// NOTE: AccountActivity is for any user to reset their password, delete their account, or log out
//  The Layout will consist of two EditTexts, one for a password, and another for a password
//  confirmation.
//  There will be three Buttons, one for resetting their own password, another to delete
//  their account, and another to log out.
//  The logout functionality has already been implemented.
//  If the reset password button is pressed, the passwords must match.
//  If delete account is pressed, the current user will be deleted using the
//  repository.deleteUser(user) method and logged out.
//  You can call the logout() method directly.
//  The Users object can be fetched from the DB using the repository.getUserByUserName(username)
//  method. The repository.updateUserPassword() method will be used to reset the password.