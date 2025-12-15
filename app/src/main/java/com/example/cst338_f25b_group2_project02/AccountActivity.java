package com.example.cst338_f25b_group2_project02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_f25b_group2_project02.databinding.ActivityAccountBinding;
import com.example.cst338_f25b_group2_project02.session.SessionManager;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // NOTE: AccountActivity is for any user to reset their password, delete their account, or log out
        //  The Layout will consist of two EditTexts, one for a password, and another for a password
        //  confirmation.
        //  There will be three Buttons, one for resetting their own password, another to delete
        //  their account, and another to log out.
        //  The logout functionality has already been implemented.
        //  If the reset password button is pressed, the passwords must match.
        //  If delete account is pressed, the current user will be deleted using the
        //  repository.deleteUser(user) method and logged out.
        //  You can call the logout() method directly.
        //  The Users object will be fetched from the DB using the repository.getUserByUserName(username)
        //  method and the Users entity setPassword(password) method will be used to reset the password.

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

    // Logout functionality
    private void showLogoutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AccountActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();
        alertBuilder.setMessage("Logout?");
        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertBuilder.create().show();
    }
    private void logout() {
        SessionManager session = SessionManager.getInstance(getApplicationContext());
        session.logout();

        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
        finish();
    }
}