package com.example.budgetingapp.DataBase;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String type;
    public String category;
    public String note;
    public long date;

    public Transaction(double amount, String type, String category, String note, long date) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.date = date;
    }
}