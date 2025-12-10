package com.example.cst338_f25b_group2_project02.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_f25b_group2_project02.database.entities.Categories;

import java.util.List;

@Dao
public interface CategoriesDAO {
    // TODO: Add queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Categories... categories);

    @Delete
    void delete(Categories category);

    @Query("SELECT * FROM " + HabitBuilderDatabase.CATEGORIES_TABLE + " ORDER BY categoryId")
    List<Categories> getAllCategories();

    @Query("DELETE FROM " + HabitBuilderDatabase.CATEGORIES_TABLE)
    void deleteAll();
}
