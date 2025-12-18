package com.example.cst338_f25b_group2_project02.session;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.LoginActivity;

public abstract class AuthenticatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SessionManager.getInstance(getApplicationContext()).isLoggedIn()) {
            startLoginAndFinish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SessionManager.getInstance(getApplicationContext()).isLoggedIn()) {
            startLoginAndFinish();
        }
    }

    private void startLoginAndFinish() {
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
        finish();
    }
}
