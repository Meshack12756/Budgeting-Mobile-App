package com.example.budgetingapp.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double targetAmount;
    public double currentAmount;
    public String deadline;

    public Goal(String name, double targetAmount, double currentAmount, String deadline) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
    }
}