package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(Context context, ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.context = context;
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @NotNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666); // each cell is 1/6 height of view
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalendarAdapter.ViewHolder holder, int position) {
        String dayOfMonth = daysOfMonth.get(position);
        holder.bind(dayOfMonth);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvCellDate;
        private final OnItemListener onItemListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            tvCellDate = itemView.findViewById(R.id.tvCellDate);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(String dayOfMonth) {
            tvCellDate.setText(dayOfMonth);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition(), tvCellDate.getText().toString());
        }
    }
}
