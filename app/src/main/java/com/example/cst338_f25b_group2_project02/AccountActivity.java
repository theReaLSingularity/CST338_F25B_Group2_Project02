package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting menu button as selected
        binding.bottomNavigationViewAccount.setSelectedItemId(R.id.account);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewAccount.setOnItemSelectedListener( item -> {
            int menuItemId = item.getItemId();

            // TODO: Implement intent factories with startActivity(intent) calls
            if (menuItemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.edit) {
                startActivity(new Intent(getApplicationContext(), EditingActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.account) {
                return true;
            }
            else if (menuItemId == R.id.manage) {
                // FIXME: Check if user is admin? here again redundant if no button?
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}