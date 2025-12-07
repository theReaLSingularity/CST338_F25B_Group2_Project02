package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivityEditingBinding;

public class EditingActivity extends AppCompatActivity {

    ActivityEditingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting menu button as selected
        binding.bottomNavigationViewEditing.setSelectedItemId(R.id.edit);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewEditing.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();
            // TODO: Replace startActivity with calls to respective Intent factories
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            else if (menuItemId == R.id.edit) {
                return false;
            }
            else if (menuItemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            }
            else if (menuItemId == R.id.manage) {
                // FIXME: Check if user is admin? here again redundant if no button?
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
            }
            return false;
        });
    }
}