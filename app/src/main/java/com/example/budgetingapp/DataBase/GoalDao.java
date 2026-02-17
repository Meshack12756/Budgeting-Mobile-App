package com.example.budgetingapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface GoalDao {

    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("SELECT * FROM goals")
    List<Goal> getAllGoals();
}