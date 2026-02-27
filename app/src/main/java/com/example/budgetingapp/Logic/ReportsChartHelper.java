package com.example.budgetingapp.Logic;

import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;

public class ReportsChartHelper {

    public static void stylePieChart(PieChart chart) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setCenterText("Expenses");
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(50f);
        chart.animateY(1000);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    public static void styleBarChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
        chart.animateY(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45f); // Prevent overlapping labels

        chart.getAxisRight().setEnabled(false);
    }

    public static void styleLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.animateX(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45f);

        chart.getAxisRight().setEnabled(false);
    }
}
