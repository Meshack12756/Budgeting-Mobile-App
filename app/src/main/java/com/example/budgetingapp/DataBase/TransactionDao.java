package com.example.budgetingapp.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transactions")
    void deleteAll();

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllLive();

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    LiveData<List<Transaction>> filterByDate(long start, long end);

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Income' COLLATE NOCASE")
    LiveData<Double> getTotalIncomeLive();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Expense' COLLATE NOCASE")
    LiveData<Double> getTotalExpensesLive();

    @Query("SELECT (SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Income' COLLATE NOCASE) - (SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Expense' COLLATE NOCASE)")
    LiveData<Double> getTotalBalanceLive();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Income' COLLATE NOCASE AND strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now')")
    LiveData<Double> getMonthlyIncomeLive();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Expense' COLLATE NOCASE AND strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now')")
    LiveData<Double> getMonthlyExpenseLive();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE category = :category")
    double getTotalByCategory(String category);

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'Expense' COLLATE NOCASE AND date(date/1000, 'unixepoch') BETWEEN :start AND :end GROUP BY category")
    List<CategoryTotal> getExpenseTotalsByCategoryBetween(String start, String end);

    @Query("SELECT strftime('%Y-%m', date/1000, 'unixepoch') as month, SUM(amount) as total FROM transactions WHERE type = 'Expense' COLLATE NOCASE AND date(date/1000, 'unixepoch') BETWEEN :start AND :end GROUP BY month")
    List<MonthlyTotal> getMonthlyExpenseTotalsBetween(String start, String end);

    @Query("SELECT date(date/1000, 'unixepoch') as date, SUM(amount) as total FROM transactions WHERE type = 'Expense' COLLATE NOCASE AND date(date/1000, 'unixepoch') BETWEEN :start AND :end GROUP BY date")
    List<DailyTotal> getDailyExpenseTotalsBetween(String start, String end);

    // Keep non-live versions for one-time fetch if needed
    @Query("SELECT (SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Income' COLLATE NOCASE) - (SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Expense' COLLATE NOCASE)")
    double getTotalBalance();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Income' COLLATE NOCASE AND strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now')")
    double getMonthlyIncome();

    @Query("SELECT IFNULL(SUM(amount), 0.0) FROM transactions WHERE type = 'Expense' COLLATE NOCASE AND strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now')")
    double getMonthlyExpense();
}
