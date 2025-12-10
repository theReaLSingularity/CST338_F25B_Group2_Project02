package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cst338_f25b_group2_project02.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ChecklistAdapter adapter;

    // TODO: Replace with actual SharedPreference check once login system is added
    private boolean isAdmin = true;   // set false to hide admin button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
}
