/**
 * <br>
 * LoginActivity provides the login screen for the Habit Builder application.
 * Users enter their username and password, which are validated against the
 * Room database through {@link com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository}.
 * <br><br>
 *
 * On successful authentication, the user's session (user ID and admin flag)
 * is stored via {@link com.example.cst338_f25b_group2_project02.session.SessionManager},
 * and the user is navigated to {@link MainActivity}. A button is also provided
 * to navigate to {@link SignupActivity} for new account creation.
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
import com.example.cst338_f25b_group2_project02.databinding.ActivityLoginBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

/**
 * Activity responsible for handling user authentication. It validates
 * credentials, manages login state, and routes users either to the
 * main application or to the sign-up screen.
 */
public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    HabitBuilderRepository repository;

    /**
     * Called when the activity is created. Sets up view binding, initializes
     * the repository, and attaches click listeners to the Log In and Sign Up
     * buttons.
     *
     * @param savedInstanceState previously saved state, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing repository
        repository = HabitBuilderRepository.getRepository(getApplication());

        // Setting Log In button on click listener
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        // Setting Sign Up button on click listener
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignupActivity.signupActivityIntentFactory(getApplicationContext()));
            }
        });
    }

    /**
     * Verifies the user's credentials against the database. If the username
     * exists and the password matches, a session is established and the user
     * is navigated to {@link MainActivity}. Otherwise, an appropriate error
     * message is displayed using a Toast.
     */
    private void verifyUser() {
        String username = binding.usernameLoginEditText.getText().toString();
        if (username.isEmpty()) {
            toastMaker("Username may not be blank.");
            return;
        }

        LiveData<Users> userObserver = repository.getUserByUserName(username);

        userObserver.observe(this, user -> {
            if (user != null) {
                String password = binding.passwordLoginEditText.getText().toString();

                if (password.equals(user.getPassword())) {
                    // Store session (user id + admin flag)
                    SessionManager session = SessionManager.getInstance(getApplicationContext());
                    session.login(user.getUserId(), user.isAdmin());

                    // Starting main activity
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext()));
                    finish();
                } else {
                    toastMaker("Invalid password.");
                }
            } else {
                toastMaker(String.format("Username %s not found", username));
            }
        });
    }

    /**
     * Utility helper for showing a short Toast message.
     *
     * @param message the text to display in the Toast
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Intent factory method for launching {@link LoginActivity}.
     *
     * @param context the Context from which the Activity is being started
     * @return an {@link Intent} configured for LoginActivity
     */
    public static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}
