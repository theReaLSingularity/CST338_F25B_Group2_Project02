package com.example.cst338_f25b_group2_project02;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;
import com.example.cst338_f25b_group2_project02.database.UsersDAO;
import com.example.cst338_f25b_group2_project02.database.entities.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HabitBuilderDatabase database;
    private UsersDAO usersDAO;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, HabitBuilderDatabase.class)
                .allowMainThreadQueries()
                .build();
        usersDAO = database.usersDAO();
    }

    @After
    public void tearDown() {
        database.close();
    }

    /**
     * Test: Insert a user
     */
    @Test
    public void insertUser() {
        // Arrange
        Users testUser = new Users("TestUser", "password123", false);

        // Act
        usersDAO.insert(testUser);

        // Assert
        assertEquals(1, usersDAO.getAllUsers().size());
    }

    /**
     * Test: Retrieve user via getUserByUsername()
     */
    @Test
    public void getUserByUsername() throws InterruptedException {
        // Arrange
        Users testUser = new Users("Lu", "password123", false);
        usersDAO.insert(testUser);

        // Act
        Users retrievedUser = LiveDataTestUtil.getValue(usersDAO.getUserByUserName("Lu"));

        // Assert
        assertNotNull(retrievedUser);
        assertEquals("Lu", retrievedUser.getUsername());
    }
}
