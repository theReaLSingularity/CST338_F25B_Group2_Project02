package com.example.cst338_f25b_group2_project02;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    HabitBuilderRepository repository;
    ChecklistAdapter adapter;

    // User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verify user already logged in or have them log in
        SessionManager session = SessionManager.getInstance(getApplicationContext());
        if (!session.isLoggedIn()) {
            startLoginActivity();
            return;
        }

        loggedInUserId = session.getUserId();

        // Initial setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());

        // Set up the navigation bar
        setUpMainActivityNavigation();

        // Set up daily checklist
        setUpDailyChecklist();

        // Observe current user
        observeCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verify user still logged in or have them log in
        SessionManager session = SessionManager.getInstance(getApplicationContext());
        if (!session.isLoggedIn()) {
            startLoginActivity();
        }
    }

    private void setUpMainActivityNavigation() {
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
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                if (!session.isAdmin()) {
                    return false;
                }
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    private void setUpDailyChecklist() {
        List<String> checklistItems = getDailyChecklist();
        adapter = new ChecklistAdapter(checklistItems);

        binding.recyclerDailyChecklist.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDailyChecklist.setAdapter(adapter);
    }

    // Starts the Login Activity
    private void startLoginActivity() {
        Intent intent = LoginActivity.loginIntentFactory((getApplicationContext()));
        startActivity(intent);
        finish();
    }

    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewHome.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
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

    // Intent Factory method for Main Activity
    static Intent mainActivityIntentFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
