package com.example.cst338_f25b_group2_project02;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    HabitBuilderRepository repository;
    ChecklistAdapter adapter;

    // User instance attributes
    private static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.cst338_f25b_group2_project02.SAVED_INSTANCE_STATE_USERID_KEY";
    private static final String MAIN_ACTIVITY_USER_ID = "com.example.cst338_f25b_group2_project02.MAIN_ACTIVITY_USER_ID";
    private static final String SHARED_PREFERENCE_USERID_KEY = "com.example.cst338_f25b_group2_project02.SHARED_PREFERENCE_USERID_KEY";
    private static final String SHARED_PREFERENCE_USERID_VALUE = "com.example.cst338_f25b_group2_project02.SHARED_PREFERENCE_USERID_VALUE";
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = -1;
    private Users user;
    // TODO: Replace with actual SharedPreference check
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing repository
        repository = HabitBuilderRepository.getRepository(getApplication());

        // Retrieving logged in user or starting Login Activity
        logInUser(savedInstanceState);
        if (loggedInUserId == -1) {
            Intent intent = LoginActivity.loginIntentFactory((getApplicationContext()));
            startActivity(intent);
        }

        // ------------------------------
        // 1. Setup Daily Checklist
        // ------------------------------
        List<String> checklistItems = getDailyChecklist();
        adapter = new ChecklistAdapter(checklistItems);

        binding.recyclerDailyChecklist.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDailyChecklist.setAdapter(adapter);

        // ------------------------------
        // 2. Configure Admin Visibility
        // ------------------------------
        if (!isAdmin) {
            binding.bottomNavigationViewHome.getMenu().removeItem(R.id.manage);
        }

        // ------------------------------
        // 3. Bottom Navigation
        // ------------------------------
        binding.bottomNavigationViewHome.setSelectedItemId(R.id.home);

        binding.bottomNavigationViewHome.setOnItemSelectedListener(item -> {
            int menuItemId = item.getItemId();

            if (menuItemId == R.id.home) {
                return true;
            }
            else if (menuItemId == R.id.edit) {
                startActivity(new Intent(getApplicationContext(), EditingActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.manage) {
                // Admin-only screen
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    // LogInUser method
    // TODO: Clean up method: comments, structure, etc.
    private void logInUser(Bundle savedInstanceState) {
        // Check shared preferences for logged in user
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                SHARED_PREFERENCE_USERID_KEY, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(SHARED_PREFERENCE_USERID_VALUE)) {
            loggedInUserId = sharedPreferences.getInt(SHARED_PREFERENCE_USERID_VALUE, LOGGED_OUT);
        }

        if (loggedInUserId == LOGGED_OUT && savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)) {
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }

        if (loggedInUserId == LOGGED_OUT) {
            // Check intent for logged in user
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }

//        if (loggedInUserId != LOGGED_OUT) {
//            return;
//        }

        if (loggedInUserId == LOGGED_OUT) {
            return;
        }
        else {
            LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);

            userObserver.observe(this, user -> {
                this.user = user;
                if (this.user != null) {
                    invalidateOptionsMenu();
                }
                else {
                    // TODO: verify this was an issue
//                    logout();
                }
            });
        }
    }

    // ------------------------------
    // Dummy data for checklist
    // Replace this with database/HabitLogs later
    // ------------------------------
    private List<String> getDailyChecklist() {
        List<String> list = new ArrayList<>();
        list.add("Drink 8 cups of water");
        list.add("Walk 10 minutes");
        list.add("Stretch for 5 minutes");
        list.add("Read for 5 minutes");
        list.add("Clean one small item");
        return list;
    }

    // NOTE: These are the logout methods to place in AccountActivity -> Log Out button
    private void showLogoutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
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
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_USERID_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(SHARED_PREFERENCE_USERID_KEY, LOGGED_OUT);
        sharedPrefEditor.apply();
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }


    // Intent Factory method for Main Activity
    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }
}
