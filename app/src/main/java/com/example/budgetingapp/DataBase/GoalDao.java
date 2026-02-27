package com.example.budgetingapp.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {

    @Insert
    long insertGoal(Goal goal);

    @Update
    void updateGoal(Goal goal);

    @Delete
    void deleteGoal(Goal goal);

    @Query("SELECT * FROM goals ORDER BY createdDate DESC")
    List<Goal> getAllGoals();

    @Query("SELECT * FROM goals WHERE goalId = :goalId")
    Goal getGoalById(int goalId);
}
