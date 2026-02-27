package com.example.budgetingapp;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = Room.databaseBuilder(
                application,
                AppDatabase.class,
                "budget_db"
        ).build();

        repository = new TransactionRepository(db.transactionDao());
        allTransactions = repository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> filterByDate(long start, long end) {
        return repository.filterByDate(start, end);
    }
    public LiveData<List<Transaction>> getTransactionsByDateRange(long start, long end) {
        return repository.getTransactionsByDateRange(start, end);
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
