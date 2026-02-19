package com.example.budgettingapp.Adapters;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.budgettingapp.Models.Budget;
import com.example.budgettingapp.R;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    public interface OnBudgetClickListener {
        void onBudgetClick(Budget budget);
    }

    private final List<Budget> budgets;
    private final OnBudgetClickListener listener;

    public BudgetAdapter(List<Budget> budgets, OnBudgetClickListener listener) {
        this.budgets = budgets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        holder.bind(budgets.get(position), listener);
    }

    @Override
    public int getItemCount() { return budgets.size(); }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName, tvAmounts, tvPercent;
        private final ProgressBar progressBar;
        private final CardView cardView;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvAmounts      = itemView.findViewById(R.id.tv_amounts);
            tvPercent      = itemView.findViewById(R.id.tv_percent);
            progressBar    = itemView.findViewById(R.id.pb_budget);
            cardView       = itemView.findViewById(R.id.card_budget_item);
        }

        void bind(Budget budget, OnBudgetClickListener listener) {
            tvCategoryName.setText(budget.getCategoryName());

            double limit   = budget.getLimitAmount();
            double spent   = budget.getAmountSpent();
            int percent    = limit > 0 ? (int) ((spent / limit) * 100) : 0;

            tvAmounts.setText(String.format("KES %.2f / KES %.2f", spent, limit));
            tvPercent.setText(percent + "%");
            progressBar.setMax(100);
            progressBar.setProgress(Math.min(percent, 100));

            int color;
            if (percent >= 90)      color = Color.parseColor("#F44336");
            else if (percent >= 70) color = Color.parseColor("#FFC107");
            else                    color = Color.parseColor("#1565C0");

            progressBar.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(color));
            tvPercent.setTextColor(color);

            cardView.setOnClickListener(v -> listener.onBudgetClick(budget));
        }
    }
}