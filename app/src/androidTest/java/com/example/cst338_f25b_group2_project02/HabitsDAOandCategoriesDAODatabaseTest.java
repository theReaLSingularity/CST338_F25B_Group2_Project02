package com.example.cst338_f25b_group2_project02;

import static com.example.cst338_f25b_group2_project02.LiveDataTestUtil.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cst338_f25b_group2_project02.database.CategoriesDAO;
import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;
import com.example.cst338_f25b_group2_project02.database.HabitsDAO;
import com.example.cst338_f25b_group2_project02.database.entities.Categories;
import com.example.cst338_f25b_group2_project02.database.entities.Habits;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class HabitsDAOandCategoriesDAODatabaseTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HabitBuilderDatabase database;
    private HabitsDAO habitsDao;
    private CategoriesDAO categoriesDao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                HabitBuilderDatabase.class
        ).allowMainThreadQueries().build();

        habitsDao = database.habitsDAO();
        categoriesDao = database.categoriesDAO();
    }

    @After
    public void tearDown() {
        database.close();
    }

    // ---------- TESTS ----------

    @Test
    public void insertHabit_andQueryByUser_returnsInsertedHabit() throws Exception {
        int userId = 2;
        int categoryId = 1;

        Habits newHabit = new Habits(
                userId,
                categoryId,
                "Eat 5 vegetables",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(90).toString(),
                true
        );

        habitsDao.insert(newHabit);

        List<Habits> activeHabits = getOrAwaitValue(habitsDao.getAllActiveHabitsForUser(userId));

        assertEquals(1, activeHabits.size());
        Habits stored = activeHabits.get(0);
        assertEquals("Eat 5 vegetables", stored.getTitle());
        assertEquals(userId, stored.getUserId());
        assertTrue(stored.isActive());
    }

    @Test
    public void updateHabit_isActiveField_reflectedInQuery() throws Exception {
        int userId = 3;
        Habits habit = new Habits(
                userId,
                1,
                "Go for a walk",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(30).toString(),
                true
        );

        long id = habitsDao.insert(habit);
        int habitId = (int) id;

        Habits stored = getOrAwaitValue(habitsDao.getHabitByHabitId(habitId));
        stored.setActive(false);
        habitsDao.insert(stored); // because insert is REPLACE

        List<Habits> activeHabits = getOrAwaitValue(habitsDao.getAllActiveHabitsForUser(userId));
        assertTrue(activeHabits.isEmpty());
    }

    @Test
    public void deleteHabit_removesFromDatabase() throws Exception {
        int userId = 4;
        Habits habit = new Habits(
                userId,
                1,
                "Practice guitar",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(60).toString(),
                true
        );

        long id = habitsDao.insert(habit);
        habit.setHabitId((int) id);

        habitsDao.delete(habit);

        List<Habits> activeHabits = getOrAwaitValue(habitsDao.getAllActiveHabitsForUser(userId));
        assertEquals(0, activeHabits.size());
    }

    @Test
    public void insertCategory_andQuery_returnsIt() {
        Categories category = new Categories("Health");
        categoriesDao.insert(category);

        List<Categories> categories = categoriesDao.getAllCategories();
        assertEquals(1, categories.size());
        assertEquals("Health", categories.get(0).getCategoryName());
    }
}