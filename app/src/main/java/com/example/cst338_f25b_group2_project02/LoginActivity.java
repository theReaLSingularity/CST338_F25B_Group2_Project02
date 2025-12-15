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

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    HabitBuilderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing repository
        repository = HabitBuilderRepository.getRepository(getApplication());

        // Setting Log In button on click listener
        binding.loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        // Setting Sign Up button on click listener
        binding.signUpButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignupActivity.signupActivityIntentFactory(getApplicationContext()));
            }
        });
    }

    // Checks user input against DB and launches Main Activity with successful credentials
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
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(),
                            user.getUserId()));
                }
                else {
                    toastMaker("Invalid password.");
                }
            }
            else {
                toastMaker(String.format("Username %s not found", username));

            }
        });
    }

    // Utility for displaying messages as a Toast text
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Intent Factory method for Login Activity
    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}