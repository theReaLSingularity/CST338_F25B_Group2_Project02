package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        binding.resetPasswordAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameAdminEditText.getText().toString();
                String newPassword = binding.passwordAdminEditText.getText().toString();
                String newPasswordConfirm = binding.confirmPasswordAdminEditText.getText().toString();

                if (validateInputs(username, newPassword, newPasswordConfirm)) {
                    resetUserPassword(username, newPassword);
                }
                else {
                    toastMaker("Password not reset");
                }
            }
        });

        binding.deleteAccountAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameAdminEditText.getText().toString();
                if (validateUsername(username)) {
                    deleteAccount(username);
                }
                else {
                    toastMaker("Account not deleted");
                }
            }
        });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    private boolean validateInputs(String username, String newPassword, String newPasswordConfirm) {
        if (username.isEmpty()) {
            toastMaker("Username is empty.");
            return false;
        }
        else if (newPassword.isEmpty()) {
            toastMaker("Enter a new password to set.");
            return false;
        }
        else if (newPasswordConfirm.isEmpty()) {
            toastMaker("Enter new password confirmation");
            return false;
        }
        else if (!newPassword.equals(newPasswordConfirm)) {
            toastMaker("Passwords do not match.");
            return false;
        }
        return true;
    }

    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            toastMaker("Username is empty.");
            return false;
        }
        return true;
    }

    private void resetUserPassword(String username, String newPassword){
        LiveData<Users> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, userToReset -> {
            if (userToReset == null) {
                toastMaker("User does not exist.");
                userObserver.removeObservers(this);
            }
            else {
                repository.updateUserPassword(userToReset.getUserId(), newPassword);
                toastMaker("Password has been reset.");
                userObserver.removeObservers(this);
            }
        });
    }

    private void deleteAccount(String username){
        LiveData<Users> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, userToDelete -> {
            if (userToDelete == null) {
                toastMaker("User does not exist.");
                userObserver.removeObservers(this);
            }
            else {
                repository.deleteUser(userToDelete);
                toastMaker("User has been deleted");
                userObserver.removeObservers(this);
            }
        });
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpManageActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewManage.setSelectedItemId(R.id.manage);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewManage.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();

            if (menuItemId == R.id.home) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
                startActivity(EditingActivity.editingActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            }
            else if (menuItemId == R.id.account) {
                startActivity(AccountActivity.accountActivityIntentFactory(getApplicationContext()));
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

    public static Intent manageActivityIntentFactory(Context context) {
        return new Intent(context, ManageActivity.class);
    }
}
