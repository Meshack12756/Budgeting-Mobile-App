package com.example.budgettingapp.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.budgettingapp.Adapters.BudgetAdapter;
import com.example.budgettingapp.DataBase.AppDatabase;
import com.example.budgettingapp.DataBase.BudgetDao;
import com.example.budgettingapp.DataBase.CategoryDao;
import com.example.budgettingapp.DataBase.TransactionDao;
import com.example.budgettingapp.Models.Budget;
import com.example.budgettingapp.Models.Category;
import com.example.budgettingapp.R;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBudgets;
    private BudgetAdapter budgetAdapter;
    private TextView tvTotalBudget, tvTotalSpent, tvBudgetMonth;
    private ProgressBar pbMonthlyOverview;

    private BudgetDao budgetDao;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // ── Initialize DB & DAOs ──────────────────────────────────────
        AppDatabase db = AppDatabase.getInstance(this);
        budgetDao      = db.budgetDao();
        categoryDao    = db.categoryDao();
        transactionDao = db.transactionDao();
        executor       = Executors.newSingleThreadExecutor();

        // ── Bind Views ────────────────────────────────────────────────
        tvTotalBudget       = findViewById(R.id.tv_total_budget);
        tvTotalSpent        = findViewById(R.id.tv_total_spent);
        tvBudgetMonth       = findViewById(R.id.tv_budget_month);
        pbMonthlyOverview   = findViewById(R.id.pb_monthly_overview);
        recyclerViewBudgets = findViewById(R.id.recycler_budgets);
        recyclerViewBudgets.setLayoutManager(new LinearLayoutManager(this));

        // ── Show current month label ──────────────────────────────────
        Calendar cal = Calendar.getInstance();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"};
        tvBudgetMonth.setText(months[cal.get(Calendar.MONTH)] + " "
                + cal.get(Calendar.YEAR));

        // ── Create Budget button ──────────────────────────────────────
        findViewById(R.id.btn_create_budget)
                .setOnClickListener(v -> showCreateBudgetDialog());

        // ── Seed test categories then load budgets ────────────────────
        // TODO: Remove this seed block before final team merge.
        // In production, categories are managed by Eugine's IncomeExpenseActivity.
        executor.execute(() -> {
            List<Category> existing = categoryDao.getAllCategories();
            if (existing.isEmpty()) {
                String[] testCategories = {
                        "Food", "Transport", "Rent",
                        "Shopping", "Entertainment", "Health", "Education"
                };
                for (String name : testCategories) {
                    Category cat = new Category();
                    cat.setName(name);
                    categoryDao.insertCategory(cat);
                }
            }
            // Load budgets AFTER seeding to guarantee categories exist
            runOnUiThread(this::loadBudgets);
        });
    }

    // ── Load all budgets from DB ──────────────────────────────────────
    private void loadBudgets() {
        executor.execute(() -> {
            List<Budget> budgets = budgetDao.getAllBudgets();
            List<Category> cats  = categoryDao.getAllCategories();

            double totalLimit = 0, totalSpent = 0;

            for (Budget budget : budgets) {
                // Returns 0 until real transactions table exists after merge
                double spent = transactionDao.getSpentByCategory();
                budget.setAmountSpent(spent);

                // Attach category name
                for (Category c : cats) {
                    if (c.getId() == budget.getCategoryId()) {
                        budget.setCategoryName(c.getName());
                        break;
                    }
                }
                totalLimit += budget.getLimitAmount();
                totalSpent += spent;
            }

            final double fLimit = totalLimit;
            final double fSpent = totalSpent;

            runOnUiThread(() -> {
                tvTotalBudget.setText(String.format("KES %.2f", fLimit));
                tvTotalSpent.setText(String.format("KES %.2f", fSpent));
                int progress = fLimit > 0 ? (int)((fSpent / fLimit) * 100) : 0;
                pbMonthlyOverview.setProgress(Math.min(progress, 100));

                budgetAdapter = new BudgetAdapter(budgets, this::onBudgetTapped);
                recyclerViewBudgets.setAdapter(budgetAdapter);
            });
        });
    }

    // ── Budget card tapped ────────────────────────────────────────────
    private void onBudgetTapped(Budget budget) {
        showEditBudgetDialog(budget);
    }

    // ── Dialog: Create new budget ─────────────────────────────────────
    private void showCreateBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_create_budget, null);
        builder.setView(dialogView).setTitle("Create Budget");

        Spinner  spinnerCategory = dialogView.findViewById(R.id.spinner_category);
        EditText etAmount        = dialogView.findViewById(R.id.et_budget_amount);
        Spinner  spinnerPeriod   = dialogView.findViewById(R.id.spinner_period);

        // Period options
        String[] periods = {"Monthly", "Weekly", "Yearly"};
        spinnerPeriod.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, periods));

        // Load categories into spinner on background thread
        executor.execute(() -> {
            List<Category> categories = categoryDao.getAllCategories();
            String[] names = new String[categories.size()];
            for (int i = 0; i < categories.size(); i++)
                names[i] = categories.get(i).getName();

            runOnUiThread(() -> spinnerCategory.setAdapter(
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, names)));
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                List<Category> cats = categoryDao.getAllCategories();
                if (cats.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "No categories found", Toast.LENGTH_SHORT).show());
                    return;
                }
                int pos        = spinnerCategory.getSelectedItemPosition();
                int categoryId = cats.get(pos < cats.size() ? pos : 0).getId();

                Budget budget = new Budget();
                budget.setCategoryId(categoryId);
                budget.setLimitAmount(Double.parseDouble(amountStr));
                budget.setPeriod((String) spinnerPeriod.getSelectedItem());
                budgetDao.insertBudget(budget);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Budget created!", Toast.LENGTH_SHORT).show();
                    loadBudgets();
                });
            });
        });

        builder.setNegativeButton("Cancel", null).show();
    }

    // ── Dialog: Edit existing budget ──────────────────────────────────
    private void showEditBudgetDialog(Budget budget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_create_budget, null);
        builder.setView(dialogView).setTitle("Edit Budget");

        EditText etAmount      = dialogView.findViewById(R.id.et_budget_amount);
        Spinner  spinnerPeriod = dialogView.findViewById(R.id.spinner_period);
        dialogView.findViewById(R.id.spinner_category).setEnabled(false);

        etAmount.setText(String.valueOf(budget.getLimitAmount()));

        String[] periods = {"Monthly", "Weekly", "Yearly"};
        spinnerPeriod.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, periods));

        // Pre-select current period
        for (int i = 0; i < periods.length; i++) {
            if (periods[i].equals(budget.getPeriod())) {
                spinnerPeriod.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Update", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }
            budget.setLimitAmount(Double.parseDouble(amountStr));
            budget.setPeriod((String) spinnerPeriod.getSelectedItem());
            executor.execute(() -> {
                budgetDao.updateBudget(budget);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Budget updated!", Toast.LENGTH_SHORT).show();
                    loadBudgets();
                });
            });
        });

        // Delete with confirmation
        builder.setNegativeButton("Delete", (dialog, which) ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Budget")
                        .setMessage("Are you sure you want to delete this budget?")
                        .setPositiveButton("Delete", (d, w) ->
                                executor.execute(() -> {
                                    budgetDao.deleteBudget(budget);
                                    runOnUiThread(() -> {
                                        Toast.makeText(this, "Budget deleted", Toast.LENGTH_SHORT).show();
                                        loadBudgets();
                                    });
                                })
                        )
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        builder.setNeutralButton("Cancel", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}