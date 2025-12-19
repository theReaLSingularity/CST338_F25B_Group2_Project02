/**
 * <br>
 * Data Access Object (DAO) for performing CRUD operations on the
 * Categories table within the Habit Builder application's Room database.
 * This DAO enables inserting, deleting, and querying category records,
 * as well as retrieving a category ID based on a case-insensitive name match.
 * <br><br>
 *
 * Used throughout the app to support habit creation, categorization, and
 * administrative management of category data.
 * <br><br>
 *
 * <b>Authors:</b> Bryan, Lee, Alexander <br>
 * <b>Course:</b> CST 338 – Software Design <br>
 * <b>Semester:</b> Fall 2025 <br>
 * <b>Last Updated:</b> 12/18/2025
 */
        package com.example.cst338_f25b_group2_project02.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.Categories;

import java.util.List;

/**
 * DAO interface defining operations for interacting with the Categories table.
 * Supports inserting new categories, deleting existing ones, retrieving all
 * category records, clearing the table, and resolving a category ID by name.
 */
@Dao
public interface CategoriesDAO {

    /**
     * Inserts one or more {@link Categories} into the database. If a conflict occurs
     * (e.g., inserting a category with a duplicate primary key), the existing record
     * is replaced.
     *
     * @param categories one or more category objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Categories... categories);

    /**
     * Deletes a specific category from the database.
     *
     * @param category the category to remove
     */
    @Delete
    void delete(Categories category);

    /**
     * Retrieves all category records stored in the database, ordered
     * by their auto-generated category ID.
     *
     * @return a list of all {@link Categories} in the database
     */
    @Query("SELECT * FROM " + HabitBuilderDatabase.CATEGORIES_TABLE + " ORDER BY categoryId")
    List<Categories> getAllCategories();

    /**
     * Deletes all entries from the Categories table.
     * Intended primarily for administrative operations or testing.
     */
    @Query("DELETE FROM " + HabitBuilderDatabase.CATEGORIES_TABLE)
    void deleteAll();

    /**
     * Retrieves the ID of a category whose name matches the provided string.
     * The comparison is case-insensitive and returns at most one match.
     *
     * @param category the category name to search for
     * @return a {@link LiveData} stream containing the matched category ID,
     *         or {@code null} if no match is found
     */
    @Query(
            "SELECT categoryId FROM " + HabitBuilderDatabase.CATEGORIES_TABLE +
                    " WHERE LOWER(categoryName) = LOWER(:category) LIMIT 1"
    )
    LiveData<Integer> getCategoryId(String category);
}
