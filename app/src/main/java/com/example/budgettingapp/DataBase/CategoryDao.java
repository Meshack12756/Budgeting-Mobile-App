package com.example.budgettingapp.DataBase;

import androidx.room.*;
import com.example.budgettingapp.Models.Category;
import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
}