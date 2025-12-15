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

        //NOTE: The EditingActivity is where the user adds and deletes habits
        //  The Layout will consist of one EditText for entering a new habit to be added.
        //  There will be one dropdown Spinner to select a category to attach to the new habit.
        //  There will be a checklist sorted by category with checkboxes to select habits.
        //  There will be a Button to delete the selected habits.
        //  On click of the Add button, the new habit and selected category (both required), will be
        //  added to the habits table, and a new entry in the habits log will be made.
        //  The fields will be cleared, and a Toast will inform the user that the habit was added.
        //  In the checklist, selected habits will be marked red and crossed-out.
        //  On press of the Delete button, a confirmation dialog will open, stating how many habits
        //  will be deleted, and if selected Delete, will be deleted from the habits and habitslog tables.
        //  Toast will confirm items were deleted.
        //  On longClick of the habits, a fragment will open to edit the habit name, category, and to reset
        //  the start date to today.

        // TODO: need to perform check to mark inactive habits that are past 90 days

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