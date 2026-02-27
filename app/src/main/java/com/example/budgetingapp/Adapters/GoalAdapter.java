package com.example.budgetingapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.DataBase.Goal;
import com.example.budgetingapp.R;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goalList;
    private OnGoalClickListener clickListener;
    private OnContributeClickListener contributeListener;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public interface OnContributeClickListener {
        void onContributeClick(Goal goal);
    }

    public GoalAdapter(List<Goal> goalList, OnGoalClickListener clickListener,
                       OnContributeClickListener contributeListener) {
        this.goalList = goalList;
        this.clickListener = clickListener;
        this.contributeListener = contributeListener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalList.get(position);

        holder.tvGoalName.setText(goal.getGoalName());
        holder.tvTargetAmount.setText(String.format("Target: KES %.2f", goal.getTargetAmount()));
        holder.tvCurrentAmount.setText(String.format("Saved: KES %.2f", goal.getCurrentAmount()));
        holder.tvDeadline.setText("Due: " + goal.getDeadline());

        int progress = (int) ((goal.getCurrentAmount() / goal.getTargetAmount()) * 100);
        holder.progressBar.setProgress(Math.min(progress, 100));
        holder.tvProgress.setText(progress + "%");

        holder.itemView.setOnClickListener(v -> clickListener.onGoalClick(goal));
        holder.btnContribute.setOnClickListener(v -> contributeListener.onContributeClick(goal));
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public void updateGoals(List<Goal> newGoalList) {
        this.goalList = newGoalList;
        notifyDataSetChanged();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvTargetAmount, tvCurrentAmount, tvDeadline, tvProgress;
        ProgressBar progressBar;
        Button btnContribute;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            tvTargetAmount = itemView.findViewById(R.id.tvTargetAmount);
            tvCurrentAmount = itemView.findViewById(R.id.tvCurrentAmount);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnContribute = itemView.findViewById(R.id.btnContribute);
        }
    }
}
