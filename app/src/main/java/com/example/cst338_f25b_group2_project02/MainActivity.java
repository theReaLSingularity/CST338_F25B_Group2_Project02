package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.cst338_f25b_group2_project02.adapters.ChecklistAdapter;
import com.example.cst338_f25b_group2_project02.adapters.ChecklistAdapter;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;


import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.util.ArrayList;

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

        setUpDailyChecklist();
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************

    private void setUpDailyChecklist() {

        // 1️⃣ Create adapter with EMPTY habit list
        adapter = new ChecklistAdapter(new ArrayList<>());

        // 2️⃣ Attach RecyclerView
        binding.recyclerDailyChecklist.setLayoutManager(
                new LinearLayoutManager(this)
        );
        binding.recyclerDailyChecklist.setAdapter(adapter);

        // 3️⃣ Observe habits from Room
        repository.getAllActiveHabitsForUser(loggedInUserId)
                .observe(this, habits -> {
                    if (habits != null) {
                        adapter.setHabits(habits);
                    }
                });
    }

    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpMainActivityNavigation() {
        binding.bottomNavigationViewHome.setSelectedItemId(R.id.home);

        binding.bottomNavigationViewHome.setOnItemSelectedListener(item -> {
            int menuItemId = item.getItemId();

            if (menuItemId == R.id.home) {
                return true;
            } else if (menuItemId == R.id.edit) {
                startActivity(EditingActivity.editingActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            } else if (menuItemId == R.id.account) {
                startActivity(AccountActivity.accountActivityIntentFactory(getApplicationContext()));
                finish();
                return true;
            } else if (menuItemId == R.id.manage) {
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                if (!session.isAdmin()) {
                    return false;
                }
                startActivity(ManageActivity.manageActivityIntentFactory(getApplicationContext()));
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
                binding.bottomNavigationViewHome
                        .getMenu()
                        .findItem(R.id.account)
                        .setTitle(user.getUsername());
            }
        });
    }

    private void setupAdminMenuItemVisibility(boolean isVisible) {
        MenuItem manageItem =
                binding.bottomNavigationViewHome.getMenu().findItem(R.id.manage);
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
