package com.example.cst338_f25b_group2_project02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

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

        binding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Makes sure Account is selected if user returns here
        binding.bottomNavigationViewAccount.setSelectedItemId(R.id.account);
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    // Reset password functionality
    private void resetPassword() {
        String newPassword = binding.passwordAccountEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordAccountEditText.getText().toString();

        // Validate password fields
        if (newPassword.isEmpty()) {
            toastMaker("Password cannot be empty");
            return;
        }

        if (confirmPassword.isEmpty()) {
            toastMaker("Please confirm your password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            toastMaker("Passwords do not match");
            binding.confirmPasswordAccountEditText.setText("");
            return;
        }

        // Clear input fields
        binding.passwordAccountEditText.setText("");
        binding.confirmPasswordAccountEditText.setText("");

        showConfirmResetPasswordDialog(newPassword);
    }
    // Reset password functionality
    private void showConfirmResetPasswordDialog(final String newPassword) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AccountActivity.this);
        alertBuilder.setTitle("Change Password");
        alertBuilder.setMessage("Are you sure you want to update your password?");

        alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update password in database
                repository.updateUserPassword(loggedInUserId, newPassword);

                binding.passwordAccountEditText.setText("");
                binding.confirmPasswordAccountEditText.setText("");
                toastMaker("Password updated");
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    // Delete account functionality
    private void showDeleteAccountDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AccountActivity.this);
        alertBuilder.setTitle("Delete Account");
        alertBuilder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    private void deleteAccount() {
        if (user != null) {
            repository.deleteUser(user);
            toastMaker("Account deleted");
            logout();
        }
    }

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

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    /**
     * Intent factory for AccountActivity
     * @param context The context from which the activity is being started
     * @return Intent configured for AccountActivity
     */
    public static Intent accountActivityIntentFactory(Context context) {
        return new Intent(context, AccountActivity.class);
    }
}
