package com.example.budgettingapp.DataBase;

import android.content.Context;
import androidx.room.*;
import com.example.budgettingapp.Models.Budget;
import com.example.budgettingapp.Models.Category;

@Database(entities = {Budget.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract BudgetDao budgetDao();
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "budgeting_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}