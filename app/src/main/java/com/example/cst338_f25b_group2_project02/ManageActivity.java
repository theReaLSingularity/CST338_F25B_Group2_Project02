/**
 * <br>
 * ManageActivity provides an admin-only screen where privileged users can
 * manage other user accounts in the Habit Builder application. From this
 * screen, an admin can:
 * <ul>
 *     <li>Reset another user's password</li>
 *     <li>Delete another user's account</li>
 * </ul>
 * All operations are performed through the
 * {@link com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository}
 * and are restricted to authenticated admin users via
 * {@link com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity}
 * and {@link com.example.cst338_f25b_group2_project02.session.SessionManager}.
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
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityManageBinding;
import com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

/**
 * Activity that exposes administrative tools for managing user accounts.
 * Admins can reset other users' passwords and delete accounts by entering
 * a username and executing the desired action.
 */
public class ManageActivity extends AuthenticatedActivity {

    ActivityManageBinding binding;
    HabitBuilderRepository repository;

    // User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;

    /**
     * Called when the activity is first created. Sets up view binding,
     * initializes the repository and session-based user state, configures
     * navigation, and wires up button actions for password reset and
     * account deletion.
     *
     * @param savedInstanceState previously saved instance state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization
        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpManageActivityNavigation();

        // Activity behavior

        binding.resetPasswordAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.usernameAdminEditText.getText().toString();
                String newPassword = binding.passwordAdminEditText.getText().toString();
                String newPasswordConfirm = binding.confirmPasswordAdminEditText.getText().toString();

                if (validateInputs(username, newPassword, newPasswordConfirm)) {
                    resetUserPassword(username, newPassword);
                } else {
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
                } else {
                    toastMaker("Account not deleted");
                }
            }
        });
    }

    // Activity-Specific Methods

    /**
     * Validates the input fields for the password reset form:
     * ensures username, new password, and confirmation are not empty
     * and that the two password fields match.
     *
     * @param username           the username of the account to modify
     * @param newPassword        the new password
     * @param newPasswordConfirm the confirmation of the new password
     * @return {@code true} if all inputs are valid, {@code false} otherwise
     */
    private boolean validateInputs(String username, String newPassword, String newPasswordConfirm) {
        if (username.isEmpty()) {
            toastMaker("Username is empty.");
            return false;
        } else if (newPassword.isEmpty()) {
            toastMaker("Enter a new password to set.");
            return false;
        } else if (newPasswordConfirm.isEmpty()) {
            toastMaker("Enter new password confirmation");
            return false;
        } else if (!newPassword.equals(newPasswordConfirm)) {
            toastMaker("Passwords do not match.");
            return false;
        }
        return true;
    }

    /**
     * Validates that the provided username is not empty.
     *
     * @param username the username to validate
     * @return {@code true} if non-empty, {@code false} otherwise
     */
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            toastMaker("Username is empty.");
            return false;
        }
        return true;
    }

    /**
     * Resets the password of the user with the given username. Looks up the
     * user via LiveData, and if found, updates their password in the database.
     * If the user does not exist, an appropriate message is shown.
     *
     * @param username   the username of the account whose password should be reset
     * @param newPassword the new password to set
     */
    private void resetUserPassword(String username, String newPassword){
        LiveData<Users> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, userToReset -> {
            if (userToReset == null) {
                toastMaker("User does not exist.");
                userObserver.removeObservers(this);
            } else {
                repository.updateUserPassword(userToReset.getUserId(), newPassword);
                toastMaker("Password has been reset.");
                userObserver.removeObservers(this);
            }
        });
    }

    /**
     * Deletes the account associated with the specified username. If the user
     * exists, that user record is removed from the database; otherwise, an
     * error Toast is displayed.
     *
     * @param username the username of the account to delete
     */
    private void deleteAccount(String username){
        LiveData<Users> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, userToDelete -> {
            if (userToDelete == null) {
                toastMaker("User does not exist.");
                userObserver.removeObservers(this);
            } else {
                repository.deleteUser(userToDelete);
                toastMaker("User has been deleted");
                userObserver.removeObservers(this);
            }
        });
    }

    /**
     * Convenience method for displaying a short Toast message.
     *
     * @param message the text to display
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Initialization Methods

    /**
     * Configures the bottom navigation bar for ManageActivity, marks the
     * Manage item as selected, and defines navigation actions for Home,
     * Edit, and Account screens. The Manage item itself is only considered
     * valid if the current session is marked as admin.
     */
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

    /**
     * Observes the currently logged-in user based on the user ID stored in
     * the session. When user data is loaded, updates the admin flag, configures
     * Manage item visibility, and sets the Account tab title to the username.
     */
    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewManage.getMenu()
                        .findItem(R.id.account)
                        .setTitle(user.getUsername());
            }
        });
    }

    /**
     * Controls the visibility and enabled state of the Manage menu item
     * in the bottom navigation based on whether the current user is an admin.
     *
     * @param isVisible {@code true} to show and enable the Manage item,
     *                  {@code false} to hide and disable it
     */
    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewManage.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // Intent Factory

    /**
     * Intent factory method for launching {@link ManageActivity}.
     *
     * @param context the Context from which this activity is being started
     * @return an {@link Intent} configured for ManageActivity
     */
    public static Intent manageActivityIntentFactory(Context context) {
        return new Intent(context, ManageActivity.class);
    }
}
