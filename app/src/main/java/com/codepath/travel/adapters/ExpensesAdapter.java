package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.models.Expense;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Expense> expenses;

    public ExpensesAdapter(Context context, ArrayList<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @NotNull
    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExpensesAdapter.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvExpense;
        private final TextView tvExpenseCost;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvExpense = itemView.findViewById(R.id.tvExpense);
            tvExpenseCost = itemView.findViewById(R.id.tvExpenseCost);
        }

        public void bind(Expense expense) {
            tvExpense.setText(expense.getName());
            tvExpenseCost.setText(String.format("%.2f", expense.getCost()));
        }
    }
}
