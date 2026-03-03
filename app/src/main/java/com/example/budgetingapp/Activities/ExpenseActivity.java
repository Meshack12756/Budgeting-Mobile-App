package com.example.budgetingapp.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.Adapters.CategoryAdapter;
import com.example.budgetingapp.DataBase.AppDatabase;
import com.example.budgetingapp.DataBase.Category;
import com.example.budgetingapp.DataBase.Transaction;
import com.example.budgetingapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseActivity extends AppCompatActivity {

    private RadioGroup typeGroup;
    private EditText etAmount;
    private RecyclerView rvCategories;
    private EditText etNotes;
    private Button btnSave;
    private CategoryAdapter categoryAdapter;
    private TextView tvSelectedDate;
    private String finalSelectedDate = "";
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        db = AppDatabase.getInstance(this);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        Button btnPickDate = findViewById(R.id.btnPickDate);

        btnPickDate.setOnClickListener(v -> showDatePicker());

        typeGroup = findViewById(R.id.typeGroup);
        etAmount = findViewById(R.id.etAmount);
        rvCategories = findViewById(R.id.rvCategories);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);

        setupCategoryRecyclerView();

        btnSave.setOnClickListener(v -> handleSave());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, monthOfYear, dayOfMonth) -> {
                    finalSelectedDate = selectedYear + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    tvSelectedDate.setText(finalSelectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupCategoryRecyclerView() {
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fetch categories from DB or provide defaults
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            if (categories.isEmpty()) {
                // Seed some categories if DB is empty
                db.categoryDao().insert(new Category("Food", 0));
                db.categoryDao().insert(new Category("Transport", 0));
                db.categoryDao().insert(new Category("Shopping", 0));
                categories = db.categoryDao().getAllCategories();
            }

            final List<Category> finalCategories = categories;
            runOnUiThread(() -> {
                categoryAdapter = new CategoryAdapter(finalCategories, category -> {});
                rvCategories.setAdapter(categoryAdapter);
            });
        });
    }

    private void handleSave() {
        boolean isIncome = typeGroup.getCheckedRadioButtonId() == R.id.radioIncome;
        String type = isIncome ? "income" : "expense";
        String amountStr = etAmount.getText().toString();
        String notes = etNotes.getText().toString();
        String selectedCategory = categoryAdapter.getSelectedCategory();

        if (amountStr.isEmpty() || selectedCategory == null || finalSelectedDate.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        // Note: For 'Transaction' constructor consistency, check your Transaction.java
        // Assuming: public Transaction(String title, double amount, String category, String type, String date)
        Transaction transaction = new Transaction(notes, amount, selectedCategory, type, finalSelectedDate);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.transactionDao().insert(transaction);
            runOnUiThread(() -> {
                Toast.makeText(ExpenseActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
