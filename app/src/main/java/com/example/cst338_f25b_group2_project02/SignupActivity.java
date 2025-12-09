package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // TODO: Database logic will be added in IL-2
        toastMaker("Validation successful! (Database logic pending)");
    }

    private boolean validateInputs(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            toastMaker("Username cannot be empty");
            binding.usernameSignupEditText.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            toastMaker("Password cannot be empty");
            binding.passwordSignupEditText.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            toastMaker("Please confirm your password");
            binding.confirmPasswordSignupEditText.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            toastMaker("Passwords do not match");
            binding.confirmPasswordSignupEditText.setText("");
            binding.confirmPasswordSignupEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Intent factory for SignupActivity
     * @param context The context from which the activity is being started
     * @return Intent configured for SignupActivity
     */
    static Intent signupActivityIntentFactory(Context context) {
        return new Intent(context, SignupActivity.class);
    }
}