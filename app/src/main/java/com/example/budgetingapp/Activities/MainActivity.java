package com.example.budgetingapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.DataBase.AppDatabase;
import com.example.budgetingapp.DataBase.Transaction;
import com.example.budgetingapp.Adapters.TransactionAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvMonthlyIncome, tvMonthlyExpense;
    private RecyclerView recyclerRecent;
    private FloatingActionButton fabAdd;
    private MaterialButton btnAddTransaction, btnViewReports;
    private TransactionAdapter transactionAdapter;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvMonthlyIncome = findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpense = findViewById(R.id.tvMonthlyExpense);
        recyclerRecent = findViewById(R.id.recyclerRecent);
        fabAdd = findViewById(R.id.fabAdd);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnViewReports = findViewById(R.id.btnViewReports);

        recyclerRecent.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        recyclerRecent.setAdapter(transactionAdapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, TransactionActivity.class))
        );

        btnAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(this, TransactionActivity.class))
        );

        btnViewReports.setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            double totalBalance = db.transactionDao().getTotalBalance();
            double monthlyIncome = db.transactionDao().getMonthlyIncome();
            double monthlyExpense = db.transactionDao().getMonthlyExpense();
            List<Transaction> recentTransactions = db.transactionDao().getAllTransactions();

            runOnUiThread(() -> {
                tvTotalBalance.setText(String.format("KES %.2f", totalBalance));
                tvMonthlyIncome.setText(String.format("Income: KES %.2f", monthlyIncome));
                tvMonthlyExpense.setText(String.format("Expense: KES %.2f", monthlyExpense));
                transactionAdapter.updateData(recentTransactions);
            });
        });
    }
}