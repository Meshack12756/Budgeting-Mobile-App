package com.example.budgetingapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.DataBase.Transaction;
import com.example.budgetingapp.Adapters.TransactionAdapter;
import com.example.budgetingapp.DataBase.TransactionViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.OnItemLongClick {

    private TextView tvTotalBalance, tvMonthlyIncome, tvMonthlyExpense;
    private TransactionAdapter transactionAdapter;
    private TransactionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Observe reactive data
        viewModel.getTotalBalance().observe(this, balance -> 
                tvTotalBalance.setText(String.format(Locale.getDefault(), "KES %.2f", balance != null ? balance : 0.0)));

        viewModel.getMonthlyIncome().observe(this, income -> 
                tvMonthlyIncome.setText(String.format(Locale.getDefault(), "Income: KES %.2f", income != null ? income : 0.0)));

        viewModel.getMonthlyExpense().observe(this, expense -> 
                tvMonthlyExpense.setText(String.format(Locale.getDefault(), "Expense: KES %.2f", expense != null ? expense : 0.0)));

        viewModel.getAllTransactions().observe(this, transactions -> 
                transactionAdapter.setList(transactions));

        // Navigation
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, TransactionActivity.class)));
        btnAddTransaction.setOnClickListener(v -> startActivity(new Intent(this, TransactionActivity.class)));
        btnViewReports.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        btnGoals.setOnClickListener(v -> startActivity(new Intent(this, GoalsActivity.class)));
        btnExpense.setOnClickListener(v -> startActivity(new Intent(this, ExpenseActivity.class)));
        btnBudget.setOnClickListener(v -> startActivity(new Intent(this, BudgetActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    public void onLongClick(Transaction transaction) {
        Toast.makeText(this, "Transaction: " + transaction.category, Toast.LENGTH_SHORT).show();
    }
}
