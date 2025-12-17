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


public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private HabitBuilderRepository repository;

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

    private void validateAndSignup() {
        String username = binding.usernameSignupEditText.getText().toString().trim();
        String password = binding.passwordSignupEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordSignupEditText.getText().toString();

        if (!validateInputs(username, password, confirmPassword)) {
            return;
        }

        createUser(username, password);
    }

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

    private void insertNewUser(String username, String password) {
        Users newUser = new Users(username, password, false);
        // Insert user into database
        repository.insertUser(newUser);

        toastMaker("Account created successfully!");
        finish();
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Intent factory for SignupActivity
     * @param context The context from which the activity is being started
     * @return Intent configured for SignupActivity
     */
    public static Intent signupActivityIntentFactory(Context context) {
        return new Intent(context, SignupActivity.class);
    }
}