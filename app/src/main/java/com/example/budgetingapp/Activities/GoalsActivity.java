package com.example.budgetingapp.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.Adapters.GoalAdapter;
import com.example.budgetingapp.DataBase.AppDatabase;
import com.example.budgetingapp.DataBase.Goal;
import com.example.budgetingapp.DataBase.GoalDao;
import com.example.budgetingapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsActivity extends AppCompatActivity implements GoalAdapter.OnGoalClickListener, GoalAdapter.OnContributeClickListener {

    private GoalAdapter adapter;
    private GoalDao goalDao;
    private List<Goal> goalList = new ArrayList<>();
    private TextView tvTotalGoals, tvTotalTarget, tvTotalSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        goalDao = AppDatabase.getInstance(this).goalDao();

        tvTotalGoals = findViewById(R.id.tvTotalGoals);
        tvTotalTarget = findViewById(R.id.tvTotalTarget);
        tvTotalSaved = findViewById(R.id.tvTotalSaved);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GoalAdapter(goalList, this, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddGoal);
        fab.setOnClickListener(v -> showGoalDialog(null));

        loadGoals();
    }

    private void loadGoals() {
        new Thread(() -> {
            goalList = goalDao.getAllGoals();
            runOnUiThread(() -> {
                adapter.updateGoals(goalList);
                updateSummary();
            });
        }).start();
    }

    private void updateSummary() {
        int count = goalList.size();
        double target = 0;
        double saved = 0;

        for (Goal g : goalList) {
            target += g.getTargetAmount();
            saved += g.getCurrentAmount();
        }

        tvTotalGoals.setText(String.valueOf(count));
        tvTotalTarget.setText(String.format(Locale.getDefault(), "KES %.0f", target));
        tvTotalSaved.setText(String.format(Locale.getDefault(), "KES %.0f", saved));
    }

    private void showGoalDialog(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goal, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etGoalName);
        EditText etTarget = view.findViewById(R.id.etTargetAmount);
        EditText etCurrent = view.findViewById(R.id.etCurrentAmount);
        EditText etDeadline = view.findViewById(R.id.etDeadline);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        if (goal != null) {
            etName.setText(goal.getGoalName());
            etTarget.setText(String.valueOf(goal.getTargetAmount()));
            etCurrent.setText(String.valueOf(goal.getCurrentAmount()));
            etDeadline.setText(goal.getDeadline());
            btnDelete.setVisibility(View.VISIBLE);
        }

        AlertDialog dialog = builder.create();

        etDeadline.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etDeadline.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String targetStr = etTarget.getText().toString().trim();
            String currentStr = etCurrent.getText().toString().trim();
            String deadline = etDeadline.getText().toString().trim();

            if (name.isEmpty() || targetStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double target = Double.parseDouble(targetStr);
                double current = currentStr.isEmpty() ? 0 : Double.parseDouble(currentStr);

                new Thread(() -> {
                    if (goal == null) {
                        Goal newGoal = new Goal();
                        newGoal.setGoalName(name);
                        newGoal.setTargetAmount(target);
                        newGoal.setCurrentAmount(current);
                        newGoal.setDeadline(deadline);
                        newGoal.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                        goalDao.insertGoal(newGoal);
                    } else {
                        goal.setGoalName(name);
                        goal.setTargetAmount(target);
                        goal.setCurrentAmount(current);
                        goal.setDeadline(deadline);
                        goalDao.updateGoal(goal);
                    }
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        loadGoals();
                    });
                }).start();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (goal != null) {
                new Thread(() -> {
                    goalDao.deleteGoal(goal);
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        loadGoals();
                    });
                }).start();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onGoalClick(Goal goal) {
        showGoalDialog(goal);
    }

    @Override
    public void onContributeClick(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Contribution");

        EditText input = new EditText(this);
        input.setHint("Amount");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    goal.setCurrentAmount(goal.getCurrentAmount() + amount);
                    new Thread(() -> {
                        goalDao.updateGoal(goal);
                        runOnUiThread(this::loadGoals);
                    }).start();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
