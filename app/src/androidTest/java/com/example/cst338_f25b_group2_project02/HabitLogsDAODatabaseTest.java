package com.example.cst338_f25b_group2_project02;

import static com.example.cst338_f25b_group2_project02.LiveDataTestUtil.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cst338_f25b_group2_project02.database.HabitBuilderDatabase;
import com.example.cst338_f25b_group2_project02.database.HabitLogsDAO;
import com.example.cst338_f25b_group2_project02.database.entities.HabitLogs;

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
public class HabitLogsDAODatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HabitBuilderDatabase database;
    private HabitLogsDAO habitLogsDao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                HabitBuilderDatabase.class
        ).allowMainThreadQueries().build();

        habitLogsDao = database.habitLogsDAO();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void insertHabitLog_andQueryToday_returnsIt() throws Exception {
        int userId = 1;
        int habitId = 10;
        String today = LocalDate.now().toString();

        HabitLogs log = new HabitLogs(habitId, userId, today, false);
        habitLogsDao.insert(log);

        List<HabitLogs> logs = getOrAwaitValue(habitLogsDao.getHabitLogsForToday(userId, today));
        assertEquals(1, logs.size());
        HabitLogs stored = logs.get(0);
        assertEquals(habitId, stored.getHabitId());
        assertEquals(userId, stored.getUserId());
        assertFalse(stored.isCompleted());
    }

    @Test
    public void updateHabitLogCompletedState_marksAsCompleted() throws Exception {
        int userId = 1;
        int habitId = 20;
        String today = LocalDate.now().toString();

        HabitLogs log = new HabitLogs(habitId, userId, today, false);
        habitLogsDao.insert(log);

        habitLogsDao.updateHabitLogCompletedState(habitId, today, true);

        List<HabitLogs> logs = getOrAwaitValue(habitLogsDao.getHabitLogsForToday(userId, today));
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).isCompleted());
    }

    @Test
    public void deleteHabitLogsByHabitId_removesAllLogsForHabit() throws Exception {
        int userId = 1;
        int habitId = 30;
        String today = LocalDate.now().toString();

        habitLogsDao.insert(
                new HabitLogs(habitId, userId, today, false),
                new HabitLogs(habitId, userId, today, true)
        );

        habitLogsDao.deleteHabitLogsByHabitId(habitId);

        List<HabitLogs> logs = getOrAwaitValue(habitLogsDao.getHabitLogsForToday(userId, today));
        assertTrue(logs.isEmpty());
    }

}