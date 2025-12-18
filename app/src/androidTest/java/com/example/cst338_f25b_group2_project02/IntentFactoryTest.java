package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation tests for Activity intent factories
 * Verifies each factory creates an Intent targeting the correct Activity.
 */
@RunWith(AndroidJUnit4.class)
public class IntentFactoryTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }
    @Test
    public void mainActivityIntentFactory_createsCorrectIntent() {
        Intent intent = MainActivity.mainActivityIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target MainActivity",
                MainActivity.class.getName(),
                intent.getComponent().getClassName());
    }

    @Test
    public void loginActivityIntentFactory_createsCorrectIntent() {
        Intent intent = LoginActivity.loginIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target LoginActivity",
                LoginActivity.class.getName(),
                intent.getComponent().getClassName());
    }

    @Test
    public void signupActivityIntentFactory_createsCorrectIntent() {
        Intent intent = SignupActivity.signupActivityIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target SignupActivity",
                SignupActivity.class.getName(),
                intent.getComponent().getClassName());
    }

    @Test
    public void accountActivityIntentFactory_createsCorrectIntent() {
        Intent intent = AccountActivity.accountActivityIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target AccountActivity",
                AccountActivity.class.getName(),
                intent.getComponent().getClassName());
    }

    @Test
    public void editingActivityIntentFactory_createsCorrectIntent() {
        Intent intent = EditingActivity.editingActivityIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target EditingActivity",
                EditingActivity.class.getName(),
                intent.getComponent().getClassName());
    }

    @Test
    public void manageActivityIntentFactory_createsCorrectIntent() {
        Intent intent = ManageActivity.manageActivityIntentFactory(context);

        assertNotNull("Intent should not be null", intent);
        assertNotNull("Intent component should not be null", intent.getComponent());
        assertEquals("Intent should target ManageActivity",
                ManageActivity.class.getName(),
                intent.getComponent().getClassName());
    }
}