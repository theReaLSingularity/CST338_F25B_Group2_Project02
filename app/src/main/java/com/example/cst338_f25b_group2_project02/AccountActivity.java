/**
 * <br>
 * AccountActivity allows the currently logged-in user to manage their account
 * within the Habit Builder application. From this screen, users can:
 * <ul>
 *     <li>Update their password</li>
 *     <li>Delete their account</li>
 *     <li>Log out of the application</li>
 * </ul>
 * The activity also configures bottom navigation, enforces authentication via
 * {@link com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity},
 * and adjusts visibility of admin-only options based on the user's role.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
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
import com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity;
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

    /**
     * Called when the activity is first created. Initializes view binding,
     * repository, session-based user state, navigation, and click listeners
     * for password reset, account deletion, and logout actions.
     *
     * @param savedInstanceState previously saved state, if any
     */
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

    /**
     * Called when the activity becomes visible again. Ensures the Account
     * item remains selected in the bottom navigation when returning to this
     * activity from other screens.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Makes sure Account is selected if user returns here
        binding.bottomNavigationViewAccount.setSelectedItemId(R.id.account);
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    /**
     * Validates the password and confirmation fields, ensuring they are not
     * empty and that they match. If validation passes, prompts the user with
     * a confirmation dialog before actually updating the password.
     */
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

    /**
     * Displays a confirmation dialog asking the user to confirm that they
     * want to update their password. If confirmed, the password is updated
     * in the database via the repository.
     *
     * @param newPassword the new password the user entered and confirmed
     */
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

    /**
     * Shows a confirmation dialog before deleting the user's account.
     * If the user confirms, {@link #deleteAccount()} is invoked.
     */
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

    /**
     * Deletes the currently loaded user account from the database using the
     * repository. If deletion succeeds, notifies the user and logs them out.
     */
    private void deleteAccount() {
        if (user != null) {
            repository.deleteUser(user);
            toastMaker("Account deleted");
            logout();
        }
    }

    /**
     * Displays a confirmation dialog before logging the user out of the
     * application. If confirmed, session state is cleared and the user is
     * returned to the Login screen.
     */
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

    /**
     * Logs the user out of the application by clearing the session via
     * {@link SessionManager}, then starts the LoginActivity and finishes
     * the current AccountActivity.
     */
    private void logout() {
        SessionManager session = SessionManager.getInstance(getApplicationContext());
        session.logout();

        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
        finish();
    }

    /**
     * Helper method to display a short Toast message.
     *
     * @param message the message text to display
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    /**
     * Configures the bottom navigation bar for AccountActivity, sets the
     * Account item as selected, and defines navigation behavior to other
     * activities (Home, Edit, Manage). The Manage item is only allowed
     * for admin users, validated via {@link SessionManager}.
     */
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

    /**
     * Observes the currently logged-in user using the repository and updates
     * local state (user object and admin flag) as well as the bottom navigation
     * UI (show/hide the Manage item and set the Account title to the username).
     */
    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewAccount.getMenu()
                        .findItem(R.id.account)
                        .setTitle(user.getUsername());
            }
        });
    }

    /**
     * Controls the visibility and enabled state of the admin-only Manage
     * menu item based on the user's admin privileges.
     *
     * @param isVisible {@code true} to make the Manage item visible and enabled,
     *                  {@code false} to hide it
     */
    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewAccount.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************

    /**
     * Intent factory for launching {@link AccountActivity}.
     *
     * @param context the Context from which the activity is being started
     * @return an {@link Intent} configured to open AccountActivity
     */
    public static Intent accountActivityIntentFactory(Context context) {
        return new Intent(context, AccountActivity.class);
    }
}
