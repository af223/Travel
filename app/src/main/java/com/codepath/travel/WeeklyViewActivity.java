package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.adapters.CalendarAdapter;
import com.codepath.travel.adapters.OnItemListener;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.codepath.travel.CalendarUtils.days;
import static com.codepath.travel.CalendarUtils.formatDate;
import static com.codepath.travel.CalendarUtils.getDaysInWeek;
import static com.codepath.travel.CalendarUtils.selectedDate;

public class WeeklyViewActivity extends AppCompatActivity implements OnItemListener {

    private TextView tvMonthYear;
    private Button btnPreviousWeek;
    private Button btnNextWeek;
    private RecyclerView rvCalendar;
    private CalendarAdapter adapter;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);

        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnPreviousWeek = findViewById(R.id.btnPreviousWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        rvCalendar = findViewById(R.id.rvCalendar);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        btnPreviousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.minusWeeks(1);
                setWeekView();
            }
        });

        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.plusWeeks(1);
                setWeekView();
            }
        });

        days = new ArrayList<>();
        adapter = new CalendarAdapter(WeeklyViewActivity.this, days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(WeeklyViewActivity.this, 7);
        rvCalendar.setLayoutManager(layoutManager);
        rvCalendar.setAdapter(adapter);

        setWeekView();
    }

    private void setWeekView() {
        tvMonthYear.setText(formatDate(selectedDate));
        getDaysInWeek(selectedDate);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        selectedDate = date;
        setWeekView();
    }
}