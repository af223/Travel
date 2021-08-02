package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.adapters.CalendarAdapter;
import com.codepath.travel.adapters.EventsAdapter;
import com.codepath.travel.adapters.OnItemListener;
import com.codepath.travel.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.codepath.travel.CalendarUtils.days;
import static com.codepath.travel.CalendarUtils.formatDate;
import static com.codepath.travel.CalendarUtils.getDaysInWeek;
import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * This activity allows the user to a see one week at the top of the screen, and the schedule for that day
 * below. This activity is reached from the itinerary fragment.
 */

public class WeeklyViewActivity extends AppCompatActivity implements OnItemListener {

    private TextView tvMonthYear;
    private ImageButton btnPreviousWeek;
    private ImageButton btnNextWeek;
    private RecyclerView rvCalendar;
    private CalendarAdapter calendarAdapter;
    private FloatingActionButton fabCreateEvent;
    private RecyclerView rvEventsList;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);

        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnPreviousWeek = findViewById(R.id.btnPreviousWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        rvCalendar = findViewById(R.id.rvCalendar);
        fabCreateEvent = findViewById(R.id.fabCreateEvent);
        rvEventsList = findViewById(R.id.rvEventsList);

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

        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeeklyViewActivity.this, CreateEventActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(WeeklyViewActivity.this, (View) fabCreateEvent, "to_create_event_transition");
                startActivity(i, options.toBundle());
            }
        });

        days = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(WeeklyViewActivity.this, days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(WeeklyViewActivity.this, 7);
        rvCalendar.setLayoutManager(layoutManager);
        rvCalendar.setAdapter(calendarAdapter);

        events = new ArrayList<>();
        eventsAdapter = new EventsAdapter(WeeklyViewActivity.this, events);
        rvEventsList.setLayoutManager(new LinearLayoutManager(this));
        rvEventsList.setAdapter(eventsAdapter);

        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEventsAdapter();
    }

    private void refreshEventsAdapter() {
        events.clear();
        events.addAll(Event.eventsForDate(selectedDate));
        eventsAdapter.notifyDataSetChanged();
    }

    private void setWeekView() {
        tvMonthYear.setText(formatDate(selectedDate));
        getDaysInWeek();
        calendarAdapter.notifyDataSetChanged();
        refreshEventsAdapter();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        selectedDate = date;
        setWeekView();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}