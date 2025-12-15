package com.example.cst338_f25b_group2_project02.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    public static final int LOGGED_OUT = -1;
    private static final String PREFS_FILE_NAME = "com.example.cst338_f25b_group2_project02.SESSION_PREFS";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_IS_ADMIN = "KEY_IS_ADMIN";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        Context appContext = context.getApplicationContext();
        prefs = appContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    // Global access point
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    // --------- Public API ---------

    public void login(int userId, boolean isAdmin) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putBoolean(KEY_IS_ADMIN, isAdmin)
                .apply();
    }

    public void logout() {
        prefs.edit()
                .putInt(KEY_USER_ID, LOGGED_OUT)
                .putBoolean(KEY_IS_ADMIN, false)
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != LOGGED_OUT;
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, LOGGED_OUT);
    }

    public boolean isAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false);
    }
}