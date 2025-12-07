package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivityManageBinding;

public class ManageActivity extends AppCompatActivity {

    ActivityManageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting menu button as selected
        binding.bottomNavigationViewManage.setSelectedItemId(R.id.manage);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewManage.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();
            // TODO: Replace startActivity with calls to respective Intent factories
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            else if (menuItemId == R.id.edit) {
                startActivity(new Intent(getApplicationContext(), EditingActivity.class));
            }
            else if (menuItemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            }
            else if (menuItemId == R.id.manage) {
                // FIXME: Check if user is admin? here again redundant if no button?
                return false;
            }
            return false;
        });
    }
}