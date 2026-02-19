package com.example.budgettingapp.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.budgettingapp.Models.Budget;
import java.util.List;

@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM budgets ORDER BY id DESC")
    List<Budget> getAllBudgets();

    @Query("SELECT * FROM budgets ORDER BY id DESC")
    LiveData<List<Budget>> getAllBudgetsLive();

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId LIMIT 1")
    Budget getBudgetByCategory(int categoryId);

    @Query("SELECT COALESCE(SUM(limit_amount), 0) FROM budgets WHERE period = 'Monthly'")
    double getTotalMonthlyBudget();
}