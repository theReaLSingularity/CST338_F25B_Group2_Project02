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
 * Unit tests for Intent Factory
 */
@RunWith(AndroidJUnit4.class)
public class IntentFactoryTest {

    private Context context;

    /**
     * Setup: Get application context
     */
    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    /**
     * Test: MainActivity.mainActivityIntentFactory() creates correct intent
     * Verifies that the intent factory creates an intent targeting MainActivity
     */
    @Test
    public void mainActivityIntentFactory() {
        // Act
        Intent intent = MainActivity.mainActivityIntentFactory(context);

        // Assert
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target MainActivity",
                MainActivity.class.getName(),
                intent.getComponent().getClassName());
    }
}