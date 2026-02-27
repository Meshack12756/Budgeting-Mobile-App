package com.example.budgetingapp.Adapters;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.*;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private List<Transaction> fullList;
    private final OnItemLongClick listener;

    public interface OnItemLongClick { void onLongClick(Transaction transaction); }

    public TransactionAdapter(List<Transaction> list, OnItemLongClick listener) {
        this.transactions = list;
        this.fullList = new ArrayList<>(list);
        this.listener = listener;
    }

    public void setList(List<Transaction> list) {
        this.transactions = list;
        this.fullList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public Transaction getTransaction(int position) { return transactions.get(position); }

    // --- Filter list for search ---
    public void filterList(String query) {
        if (query.isEmpty()) {
            transactions = new ArrayList<>(fullList);
        } else {
            List<Transaction> filtered = new ArrayList<>();
            for (Transaction t : fullList) {
                if (t.category.toLowerCase().contains(query.toLowerCase()) ||
                        t.note.toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(t);
                }
            }
            transactions = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
        holder.amount.setText(format.format(t.amount));
        holder.category.setText(t.category);
        holder.date.setText(new Date(t.date).toString());

        holder.amount.setTextColor(t.type.equals("Income") ?
                Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));

        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).start();

        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(t);
            return true;
        });
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, category, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.textAmount);
            category = itemView.findViewById(R.id.textCategory);
            date = itemView.findViewById(R.id.textDate);
        }
    }
}
