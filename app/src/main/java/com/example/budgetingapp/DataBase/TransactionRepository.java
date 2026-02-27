package com.example.budgetingapp.DataBase;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import java.util.List;

public class TransactionRepository {

    private final TransactionDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(TransactionDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return dao.getAllLive();
    }

    public LiveData<List<Transaction>> filterByDate(long start, long end) {
        return dao.filterByDate(start, end);
    }
    public LiveData<List<Transaction>> getTransactionsByDateRange(long start, long end) {
        return dao.filterByDate(start, end);
    }

    public void insert(Transaction transaction) {
        executor.execute(() -> dao.insert(transaction));
    }

    public void update(Transaction transaction) {
        executor.execute(() -> dao.update(transaction));
    }

    public void delete(Transaction transaction) {
        executor.execute(() -> dao.delete(transaction));
    }
}
