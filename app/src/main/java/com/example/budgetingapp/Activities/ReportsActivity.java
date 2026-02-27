package com.example.budgetingapp.Activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetingapp.Logic.*;
import com.example.budgetingapp.DataBase.*;
import com.example.budgetingapp.R;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    private TransactionDao dao;

    private String startDate = "2026-01-01";
    private String endDate = "2026-12-31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dao = AppDatabase.getInstance(this).transactionDao();
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);

        setupTabs();
        setupDatePickers();
        setupExport();

        // Sample data insertion for testing
        new Thread(() -> {
            dao.deleteAll();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                dao.insert(new Transaction(1000, "expense", "Food", "Food", sdf.parse("2026-02-20").getTime()));
                dao.insert(new Transaction(5000, "expense", "Housing", "Rent", sdf.parse("2026-02-21").getTime()));
                dao.insert(new Transaction(20000, "income", "Job", "Salary", sdf.parse("2026-02-19").getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            runOnUiThread(this::reloadCharts);
        }).start();
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Pie"));
        tabLayout.addTab(tabLayout.newTab().setText("Bar"));
        tabLayout.addTab(tabLayout.newTab().setText("Line"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                pieChart.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
                barChart.setVisibility(pos == 1 ? View.VISIBLE : View.GONE);
                lineChart.setVisibility(pos == 2 ? View.VISIBLE : View.GONE);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void reloadCharts() {
        loadPie();
        loadBar();
        loadLine();
    }

    private void loadPie() {
        new Thread(() -> {
            List<CategoryTotal> list = dao.getExpenseTotalsByCategoryBetween(startDate, endDate);
            runOnUiThread(() -> {
                if (list == null || list.isEmpty()) { pieChart.clear(); return; }
                ArrayList<PieEntry> entries = new ArrayList<>();
                for (CategoryTotal ct : list)
                    entries.add(new PieEntry((float) ct.total, ct.category));

                PieDataSet ds = new PieDataSet(entries, "");
                ds.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
                ds.setValueTextSize(12f);

                pieChart.setData(new PieData(ds));
                ReportsChartHelper.stylePieChart(pieChart);
                pieChart.invalidate();
            });
        }).start();
    }

    private void loadBar() {
        new Thread(() -> {
            List<MonthlyTotal> list = dao.getMonthlyExpenseTotalsBetween(startDate, endDate);
            runOnUiThread(() -> {
                if (list == null || list.isEmpty()) { barChart.clear(); return; }
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    entries.add(new BarEntry(i, (float) list.get(i).total));
                    labels.add(list.get(i).month);
                }

                BarDataSet ds = new BarDataSet(entries, "Monthly Expenses");
                ds.setColor(Color.parseColor("#BC224E")); 

                barChart.setData(new BarData(ds));
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                ReportsChartHelper.styleBarChart(barChart);
                barChart.invalidate();
            });
        }).start();
    }

    private void loadLine() {
        new Thread(() -> {
            List<DailyTotal> list = dao.getDailyExpenseTotalsBetween(startDate, endDate);
            runOnUiThread(() -> {
                if (list == null || list.isEmpty()) { lineChart.clear(); return; }

                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    entries.add(new Entry(i, (float) list.get(i).total));
                    labels.add(list.get(i).date);
                }

                LineDataSet ds = new LineDataSet(entries, "Daily Spending");

                ds.setDrawFilled(false);      
                ds.setDrawCircles(true);
                ds.setCircleRadius(5f);
                ds.setLineWidth(3f);
                ds.setColor(Color.BLUE);
                ds.setCircleColor(Color.BLUE);
                ds.setDrawValues(true);

                lineChart.setData(new LineData(ds));
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                ReportsChartHelper.styleLineChart(lineChart);
                lineChart.invalidate();
            });
        }).start();
    }

    private void setupDatePickers() {
        findViewById(R.id.btnStartDate).setOnClickListener(v -> pickDate(true));
        findViewById(R.id.btnEndDate).setOnClickListener(v -> pickDate(false));
    }

    private void pickDate(boolean isStart) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
            if (isStart) startDate = date; else endDate = date;
            reloadCharts();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupExport() {
        findViewById(R.id.btnExport).setOnClickListener(v ->
                Toast.makeText(this, "Exporting to PDF...", Toast.LENGTH_SHORT).show());
    }
}
