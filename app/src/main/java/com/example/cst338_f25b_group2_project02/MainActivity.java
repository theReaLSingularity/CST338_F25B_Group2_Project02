package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cst338_f25b_group2_project02.adapters.ChecklistAdapter;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;
import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import com.example.cst338_f25b_group2_project02.models.HabitLogChecklistItem;
import com.example.cst338_f25b_group2_project02.session.AuthenticatedActivity;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: need to standardize document comments, document headers and place throughout

    // TODO: need to perform check to mark as inactive the habits that are past 90 days

    // TODO: need to create Checklist Adapater for MainActivity to mark habitLog as completed for day

    // TODO: need to create 90 logs on New Habit insertion

    // TODO: need to implement database foreign keys relation

    // TODO: need to complete unit tests

public class MainActivity extends AuthenticatedActivity {

    ActivityMainBinding binding;
    HabitBuilderRepository repository;

    // Adding RecyclerView and ChecklistAdapter
    ChecklistAdapter adapter;
    RecyclerView recyclerView;


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

        // Adding the main recycler view
        recyclerView = findViewById(R.id.recyclerDailyChecklist);

        adapter = new ChecklistAdapter((item, isChecked) -> {
            repository.updateHabitLogCompletedState(
                    item.getHabitId(),
                    item.getDate(),
                    isChecked
            );
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Map<Integer, String> habitTitles = new HashMap<>();

        repository.getAllActiveHabitsForUser(loggedInUserId).observe(this, habits -> {
            habitTitles.clear();
            if (habits != null) {
                for (Habits h : habits) {
                    habitTitles.put(h.getHabitId(), h.getTitle());
                }
            }
        });

        repository.getUserHabitsForToday(loggedInUserId, LocalDate.now().toString())
                .observe(this, habitLogs -> {
                    if (habitLogs != null) {
                        List<HabitLogChecklistItem> checklistItems = new ArrayList<>();
                        for (HabitLogs log : habitLogs) {
                            String title = habitTitles.getOrDefault(log.getHabitId(), "Habit");
                            checklistItems.add(new HabitLogChecklistItem(title, log));
                        }
                        adapter.setHabitLogs(checklistItems);
                    }
                });
    }

    // *************************************
    //      Activity-Specific Methods
    // *************************************



    // *************************************
    //       Initialization Methods
    // *************************************

    private void setUpMainActivityNavigation() {
        // Setting menu button as selected
        binding.bottomNavigationViewHome.setSelectedItemId(R.id.home);

        // Implementing bottom navigation menu action
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
    public static Intent mainActivityIntentFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
