package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AuthenticatedActivity {

    ActivityMainBinding binding;
    HabitBuilderRepository repository;
    ChecklistAdapter adapter;

    // *************************************
    //      User instance attributes
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = LOGGED_OUT;
    private Users user;
    private boolean isAdmin;
    // *************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *********************************
        //         Initialization
        // *********************************

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HabitBuilderRepository.getRepository(getApplication());
        loggedInUserId = SessionManager.getInstance(getApplicationContext()).getUserId();
        observeCurrentUser();
        setUpMainActivityNavigation();

        // *********************************
        //           Activity
        // *********************************

        // TODO: Initialize activity methods here

        setUpDailyChecklist();
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    // Sets up daily checklist
    private void setUpDailyChecklist() {
        List<String> checklistItems = getDailyChecklist();
        adapter = new ChecklistAdapter(checklistItems);

        binding.recyclerDailyChecklist.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDailyChecklist.setAdapter(adapter);
    }

    // Gets daily checklist
    private List<String> getDailyChecklist() {
        // ------------------------------
        // Dummy data for checklist
        // Replace this with database/HabitLogs later
        // ------------------------------
        List<String> list = new ArrayList<>();
        list.add("Drink 8 cups of water");
        list.add("Walk 10 minutes");
        list.add("Stretch for 5 minutes");
        list.add("Read for 5 minutes");
        list.add("Clean one small item");
        return list;
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpMainActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewHome.setSelectedItemId(R.id.home);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewHome.setOnItemSelectedListener(item -> {
            int menuItemId = item.getItemId();

            // TODO: Implement intent factories with startActivity(intent) calls
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

    private void observeCurrentUser() {
        LiveData<Users> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.isAdmin = user.isAdmin();
                setupAdminMenuItemVisibility(this.isAdmin);
                // Setting username as account title
                binding.bottomNavigationViewHome.getMenu().findItem(R.id.account).setTitle(user.getUsername());

            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem = binding.bottomNavigationViewHome.getMenu().findItem(R.id.manage);
        manageItem.setEnabled(isVisible);
        manageItem.setVisible(isVisible);
    }

    // *************************************
    //          Intent Factory
    // *************************************
    static Intent mainActivityIntentFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }
}

// NOTE: MainActivity is...
//
//
