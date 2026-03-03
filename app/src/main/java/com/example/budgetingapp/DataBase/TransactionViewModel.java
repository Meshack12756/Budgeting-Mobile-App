package com.example.budgetingapp.DataBase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<Double> totalBalance;
    private final LiveData<Double> monthlyIncome;
    private final LiveData<Double> monthlyExpense;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        TransactionDao dao = db.transactionDao();
        repository = new TransactionRepository(dao);
        
        allTransactions = dao.getAllLive();
        totalBalance = dao.getTotalBalanceLive();
        monthlyIncome = dao.getMonthlyIncomeLive();
        monthlyExpense = dao.getMonthlyExpenseLive();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<Double> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<Double> getMonthlyIncome() {
        return monthlyIncome;
    }

    public LiveData<Double> getMonthlyExpense() {
        return monthlyExpense;
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
    
    public LiveData<List<Transaction>> filterByDate(long start, long end) {
        return repository.filterByDate(start, end);
    }
}
