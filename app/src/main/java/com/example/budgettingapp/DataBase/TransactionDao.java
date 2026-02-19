package com.example.budgettingapp.DataBase;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface TransactionDao {

    @Query("SELECT 0")
    double getSpentByCategory();
}