package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.codepath.travel.CalendarUtils.selectedDate;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalendarAdapter(Context context, ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.context = context;
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @NotNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (days.size() > 8) { // month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666); // each cell is 1/6 height of view
        } else { // week view
            layoutParams.height = (int) parent.getHeight();
        }
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalendarAdapter.ViewHolder holder, int position) {
        LocalDate date = days.get(position);
        holder.bind(date);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvCellDate;
        private final OnItemListener onItemListener;
        private final View clCalendarCell;

        public ViewHolder(@NonNull @NotNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            tvCellDate = itemView.findViewById(R.id.tvCellDate);
            clCalendarCell = itemView.findViewById(R.id.clCalendarCell);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(LocalDate date) {
            if (date == null) {
                tvCellDate.setText("");
            } else {
                tvCellDate.setText(String.valueOf(date.getDayOfMonth()));
                if (date.equals(selectedDate)) {
                    clCalendarCell.setBackgroundColor(Color.LTGRAY);
                } else {
                    clCalendarCell.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition(), tvCellDate.getText().toString());
        }
    }
}
