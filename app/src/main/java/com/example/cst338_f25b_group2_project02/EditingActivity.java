package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.databinding.ActivityEditingBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

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

            // TODO: Implement intent factories with startActivity(intent) calls
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
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
}