/**
 * <br>
 * SignupActivity provides the user registration screen for the Habit Builder
 * application. From this screen, a new user can:
 * <ul>
 *     <li>Enter a unique username</li>
 *     <li>Create and confirm a password</li>
 *     <li>Have their new account stored in the Room database</li>
 * </ul>
 * The activity validates user input, checks that the username does not already
 * exist via {@link com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository},
 * and creates a non-admin {@link com.example.cst338_f25b_group2_project02.database.entities.Users}
 * record on success.
 * <br><br>
 *
 * After a successful sign-up, the activity finishes and returns control to
 * the Login screen.
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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivitySignupBinding;

/**
 * Activity responsible for registering new users. It validates username and
 * password input, ensures the username is unique, and inserts a new user into
 * the database if all checks pass.
 */
public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private HabitBuilderRepository repository;

    /**
     * Called when the activity is created. Sets up view binding, initializes
     * the repository, and wires the Sign Up button to trigger validation and
     * registration logic.
     *
     * @param savedInstanceState previously saved instance state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = HabitBuilderRepository.getRepository(getApplication());

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSignup();
            }
        });
    }

    /**
     * Reads the username and password fields from the UI, validates them,
     * and if the inputs are valid, proceeds to create the user.
     */
    private void validateAndSignup() {
        String username = binding.usernameSignupEditText.getText().toString().trim();
        String password = binding.passwordSignupEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordSignupEditText.getText().toString();

        if (!validateInputs(username, password, confirmPassword)) {
            return;
        }

        createUser(username, password);
    }

    /**
     * Validates the username, password, and password confirmation fields.
     * Ensures none are empty and that the two password fields match.
     *
     * @param username        the username entered by the user
     * @param password        the password entered by the user
     * @param confirmPassword the repeated password for confirmation
     * @return {@code true} if all inputs are valid, {@code false} otherwise
     */
    private boolean validateInputs(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            toastMaker("Username is empty");
            return false;
        }

        if (password.isEmpty()) {
            toastMaker("Password is empty");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            toastMaker("Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            toastMaker("Passwords do not match!");
            binding.confirmPasswordSignupEditText.setText("");
            return false;
        }

        return true;
    }

    /**
     * Checks whether the provided username already exists in the database.
     * If the username is unique, a new user is inserted; otherwise, the
     * user is prompted to choose another username.
     *
     * @param username the desired username
     * @param password the password to associate with the new account
     */
    private void createUser(String username, String password) {
        LiveData<Users> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, existingUser -> {
            if (existingUser != null) {
                // Username already exists
                toastMaker("Username already exists. Please choose another.");
                binding.usernameSignupEditText.requestFocus();
                userObserver.removeObservers(this);
            } else {
                // Username is unique → insert and back to login
                insertNewUser(username, password);
                userObserver.removeObservers(this);
            }
        });
    }

    /**
     * Inserts a new non-admin user into the database and finishes this
     * activity, returning control to the Login screen.
     *
     * @param username the new user's username
     * @param password the new user's password
     */
    private void insertNewUser(String username, String password) {
        Users newUser = new Users(username, password, false);
        // Insert user into database
        repository.insertUser(newUser);

        toastMaker("Account created successfully!");
        finish();
    }

    /**
     * Helper method to display a short Toast message.
     *
     * @param message the text to show in the Toast
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Intent factory for {@link SignupActivity}.
     *
     * @param context The context from which the activity is being started
     * @return Intent configured for SignupActivity
     */
    public static Intent signupActivityIntentFactory(Context context) {
        return new Intent(context, SignupActivity.class);
    }
}
