package com.example.cst338_f25b_group2_project02;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivityManageBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

public class ManageActivity extends AppCompatActivity {

    ActivityManageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // NOTE: ManagageActivity is available to Admins ONLY
        //  The Layout will consist of three EditTexts, one for a username, one for a password,
        //  and another for a password confirmation.
        //  There will be two Buttons, one for resetting the users password and another to delete
        //  the user.
        //  If the reset password is pressed, the username must exist in the database, and the
        //  passwords must match.
        //  If delete account is pressed, the user will be deleted using the repository.deleteUser(user)
        //  method.
        //  The Users object will be fetched from the DB using the repository.getUserByUserName(username)
        //  method and the Users entity setPassword(password) method will be used to reset the password.

        // Setting menu button as selected
        binding.bottomNavigationViewManage.setSelectedItemId(R.id.manage);

        // Implementing bottom navigation menu action
        binding.bottomNavigationViewManage.setOnItemSelectedListener( item -> {
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
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                finish();
                return true;
            }
            else if (menuItemId == R.id.manage) {
                SessionManager session = SessionManager.getInstance(getApplicationContext());
                if (!session.isAdmin()) {
                    return false;
                }
                return true;
            }
            return false;
        });
    }
}