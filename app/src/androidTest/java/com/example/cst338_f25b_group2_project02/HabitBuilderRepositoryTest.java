package com.example.cst338_f25b_group2_project02;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderRepository;
import com.example.cst338_f25b_group2_project02.database.entities.Users;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit tests for HabitBuilderRepository user methods
 * Tests insertUser method using an in-memory Room database
 */
@RunWith(AndroidJUnit4.class)
public class HabitBuilderRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HabitBuilderDatabase database;
    private HabitBuilderRepository repository;

    /**
     * Setup: Create in-memory database and repository before each test
     */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();

        // Create in-memory database (does not persist between tests)
        database = Room.inMemoryDatabaseBuilder(context, HabitBuilderDatabase.class)
                .allowMainThreadQueries()
                .build();

        // Create repository using the in-memory database
        repository = new HabitBuilderRepository(database);
    }

    /**
     * Teardown: Close database after each test
     */
    @After
    public void tearDown() {
        database.close();
    }

    /**
     * Test: Insert user through repository (createUser equivalent)
     * This tests the insertUser() method using an in-memory Room database
     */
    @Test
    public void insertUser() throws InterruptedException {
        // Arrange - Create a test user
        Users newUser = new Users("testuser", "testpass", false);

        // Act - Insert user via repository
        repository.insertUser(newUser);

        // Wait for async operation to complete
        TimeUnit.MILLISECONDS.sleep(500);

        // Assert - Retrieve user and verify insertion
        Users retrievedUser = LiveDataTestUtil.getOrAwaitValue(
                repository.getUserByUserName("testuser")
        );

        assertNotNull("User should be inserted into in-memory database", retrievedUser);
        assertEquals("Username should match", "testuser", retrievedUser.getUsername());
        assertEquals("Password should match", "testpass", retrievedUser.getPassword());
        assertFalse("User should not be admin", retrievedUser.isAdmin());
    }

    /**
     * Test: Retrieve non-existent user returns null
     */
    @Test
    public void getUserByUserName_nonExistentUser() throws InterruptedException {
        // Act - Try to retrieve a user that doesn't exist
        Users retrievedUser = LiveDataTestUtil.getOrAwaitValue(
                repository.getUserByUserName("nonexistent")
        );

        // Assert - Should return null
        assertNull("Non-existent user should return null", retrievedUser);
    }
}