package com.example.budgetingapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions WHERE type='expense'")
    double getTotalExpensees();

    @Query("SELECT SUM(amount) FROM transactions WHERE type='income'")
    double getTotalIncome();

    @Query("SELECT (SELECT SUM(amount) FROM transactions WHERE type = 'income') - (SELECT SUM(amount) FROM transactions WHERE type = 'expense')")
    double getTotalBalance();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')")
    double getMonthlyIncome();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')")
    double getMonthlyExpense();

    @Query("SELECT SUM(amount) FROM transactions WHERE category = :categoryName")
    double getTotalByCategory(String categoryName);
}