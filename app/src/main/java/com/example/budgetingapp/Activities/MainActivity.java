package com.example.budgetingapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.OnItemLongClick {

    private TextView tvTotalBalance, tvMonthlyIncome, tvMonthlyExpense;
    private TransactionAdapter transactionAdapter;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvMonthlyIncome = findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpense = findViewById(R.id.tvMonthlyExpense);
        RecyclerView recyclerRecent = findViewById(R.id.recyclerRecent);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        
        MaterialButton btnAddTransaction = findViewById(R.id.btnAddTransaction);
        MaterialButton btnViewReports = findViewById(R.id.btnViewReports);
        MaterialButton btnGoals = findViewById(R.id.btnGoals);
        MaterialButton btnExpense = findViewById(R.id.btnExpense);
        MaterialButton btnBudget = findViewById(R.id.btnBudget);
        MaterialButton btnSettings = findViewById(R.id.btnSettings);

        recyclerRecent.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(new ArrayList<>(), this);
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

        btnGoals.setOnClickListener(v ->
                startActivity(new Intent(this, GoalsActivity.class))
        );

        btnExpense.setOnClickListener(v ->
                startActivity(new Intent(this, ExpenseActivity.class))
        );

        btnBudget.setOnClickListener(v ->
                startActivity(new Intent(this, BudgetActivity.class))
        );

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        executor.execute(() -> {
            double totalBalance = db.transactionDao().getTotalBalance();
            double monthlyIncome = db.transactionDao().getMonthlyIncome();
            double monthlyExpense = db.transactionDao().getMonthlyExpense();
            List<Transaction> recentTransactions = db.transactionDao().getAllTransactions();

            runOnUiThread(() -> {
                tvTotalBalance.setText(String.format(Locale.getDefault(), "KES %.2f", totalBalance));
                tvMonthlyIncome.setText(String.format(Locale.getDefault(), "Income: KES %.2f", monthlyIncome));
                tvMonthlyExpense.setText(String.format(Locale.getDefault(), "Expense: KES %.2f", monthlyExpense));
                transactionAdapter.setList(recentTransactions);
            });
        });
    }

    @Override
    public void onLongClick(Transaction transaction) {
        Toast.makeText(this, "Transaction: " + transaction.category, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
