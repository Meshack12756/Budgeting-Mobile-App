package com.example.budgettingapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "limit_amount")
    private double limitAmount;

    @ColumnInfo(name = "period")
    private String period;

    @Ignore
    private double amountSpent;

    @Ignore
    private String categoryName;

    public Budget() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public double getAmountSpent() { return amountSpent; }
    public void setAmountSpent(double amountSpent) { this.amountSpent = amountSpent; }

    public String getCategoryName() { return categoryName != null ? categoryName : "Uncategorized"; }
    public void setCategoryName(String name) { this.categoryName = name; }

    public int getUsagePercent() {
        if (limitAmount <= 0) return 0;
        return (int) ((amountSpent / limitAmount) * 100);
    }

    public boolean isOverBudget() {
        return amountSpent > limitAmount;
    }
}