package com.example.budgetingapp.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double budgetLimit;

    public Category(String name, double budgetLimit) {
        this.name = name;
        this.budgetLimit = budgetLimit;
    }
}
