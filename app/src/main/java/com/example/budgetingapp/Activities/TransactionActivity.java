package com.example.budgetingapp.Activities;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionActivity extends AppCompatActivity {

    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private RecyclerView recyclerView;

    private EditText searchBar;
    private Button btnStartDate, btnEndDate;
    private Chip chipAll, chipIncome, chipExpense;

    private long startDate = 0, endDate = 0;
    private String selectedType = "All";
    private String searchQuery = "";

    private final ArrayList<Transaction> fullTransactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TransactionAdapter(new ArrayList<>(), this::showEditDialog);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchEditText);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);

        chipAll = findViewById(R.id.chipAll);
        chipIncome = findViewById(R.id.chipIncome);
        chipExpense = findViewById(R.id.chipExpense);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Observe database
        viewModel.getAllTransactions().observe(this, transactions -> {
            fullTransactionList.clear();
            fullTransactionList.addAll(transactions);
            updateFilteredList();
        });

        setupSwipe();

        setupSearch();
        setupTypeChips();
        setupDateButtons();

        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());
    }

    // ================= SEARCH =================

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                updateFilteredList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ================= TYPE FILTER =================

    private void setupTypeChips() {
        chipAll.setOnClickListener(v -> {
            selectedType = "All";
            updateFilteredList();
        });

        chipIncome.setOnClickListener(v -> {
            selectedType = "Income";
            updateFilteredList();
        });

        chipExpense.setOnClickListener(v -> {
            selectedType = "Expense";
            updateFilteredList();
        });
    }

    // ================= DATE FILTER =================

    private void setupDateButtons() {

        // Hide ❌ initially
        btnStartDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnEndDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        // Detect ❌ click for Start Date
        btnStartDate.setOnTouchListener((v, event) -> {
            if (btnStartDate.getCompoundDrawables()[2] != null) {
                int drawableWidth = btnStartDate.getCompoundDrawables()[2].getBounds().width();
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getX() >= (btnStartDate.getWidth() - drawableWidth - btnStartDate.getPaddingEnd())) {
                    clearStartDate();
                    return true;
                }
            }
            return false;
        });

        // Detect ❌ click for End Date
        btnEndDate.setOnTouchListener((v, event) -> {
            if (btnEndDate.getCompoundDrawables()[2] != null) {
                int drawableWidth = btnEndDate.getCompoundDrawables()[2].getBounds().width();
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getX() >= (btnEndDate.getWidth() - drawableWidth - btnEndDate.getPaddingEnd())) {
                    clearEndDate();
                    return true;
                }
            }
            return false;
        });
    }

    private void showDatePicker(boolean isStart) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStart ? "Select Start Date" : "Select End Date")
                .build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            if (isStart) {
                startDate = selection;
                btnStartDate.setText(formatDate(startDate));
                btnStartDate.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
            } else {
                endDate = selection;
                btnEndDate.setText(formatDate(endDate));
                btnEndDate.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
            }

            updateFilteredList();
        });
    }

    private void clearStartDate() {
        startDate = 0;
        btnStartDate.setText("Start Date");
        btnStartDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        updateFilteredList();
    }

    private void clearEndDate() {
        endDate = 0;
        btnEndDate.setText("End Date");
        btnEndDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        updateFilteredList();
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                .format(new Date(millis));
    }

    // ================= FILTER LOGIC =================

    private void updateFilteredList() {
        ArrayList<Transaction> filtered = new ArrayList<>();

        for (Transaction t : fullTransactionList) {

            boolean matchesType =
                    selectedType.equals("All") || t.type.equals(selectedType);

            boolean matchesSearch =
                    t.category.toLowerCase().contains(searchQuery) ||
                            t.note.toLowerCase().contains(searchQuery);

            boolean matchesDate =
                    (startDate == 0 || t.date >= startDate) &&
                            (endDate == 0 || t.date <= endDate);

            if (matchesType && matchesSearch && matchesDate) {
                filtered.add(t);
            }
        }

        adapter.setList(filtered);
    }

    // ================= ADD / EDIT =================

    private void showAddDialog() {
        showDialog(null);
    }

    private void showEditDialog(Transaction transaction) {
        showDialog(transaction);
    }

    private void showDialog(Transaction transaction) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.bottomsheet_add_edit, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        EditText amount = view.findViewById(R.id.editAmount);
        Spinner type = view.findViewById(R.id.spinnerType);
        EditText category = view.findViewById(R.id.editCategory);
        EditText note = view.findViewById(R.id.editNote);
        Button save = view.findViewById(R.id.btnSave);

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[]{"Income", "Expense"});

        type.setAdapter(spinnerAdapter);

        if (transaction != null) {
            amount.setText(String.valueOf(transaction.amount));
            category.setText(transaction.category);
            note.setText(transaction.note);
        }

        save.setOnClickListener(v -> {

            if (amount.getText().toString().isEmpty()) {
                amount.setError("Amount required");
                return;
            }

            double amt;
            try {
                amt = Double.parseDouble(amount.getText().toString());
                if (amt <= 0) {
                    amount.setError("Must be > 0");
                    return;
                }
            } catch (Exception e) {
                amount.setError("Invalid number");
                return;
            }

            String t = type.getSelectedItem().toString();
            String cat = category.getText().toString();
            String n = note.getText().toString();

            if (cat.isEmpty()) {
                category.setError("Category required");
                return;
            }

            if (transaction == null) {
                viewModel.insert(
                        new Transaction(amt, t, cat, n, System.currentTimeMillis())
                );
            } else {
                transaction.amount = amt;
                transaction.type = t;
                transaction.category = cat;
                transaction.note = n;
                viewModel.update(transaction);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    // ================= SWIPE DELETE =================

    private void setupSwipe() {

        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder holder,
                                         int direction) {

                        Transaction transaction =
                                adapter.getTransaction(holder.getAdapterPosition());

                        new AlertDialog.Builder(TransactionActivity.this)
                                .setTitle("Confirm Delete")
                                .setMessage("Delete this transaction?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    viewModel.delete(transaction);
                                    Snackbar.make(recyclerView,
                                                    "Deleted",
                                                    Snackbar.LENGTH_LONG)
                                            .setAction("UNDO",
                                                    v -> viewModel.insert(transaction))
                                            .show();
                                })
                                .setNegativeButton("Cancel",
                                        (dialog, which) ->
                                                adapter.notifyItemChanged(holder.getAdapterPosition()))
                                .setCancelable(false)
                                .show();
                    }
                }).attachToRecyclerView(recyclerView);
    }
}
