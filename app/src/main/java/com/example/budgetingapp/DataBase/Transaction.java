package com.example.budgetingapp.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double amount;
    public String category;
    public String type; // is it income or expense
    public String date;

    public Transaction(String title, double amount, String category, String type, String date) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
    }
}