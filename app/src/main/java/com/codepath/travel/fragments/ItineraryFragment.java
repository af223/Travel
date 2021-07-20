package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.WeeklyViewActivity;
import com.codepath.travel.adapters.CalendarAdapter;
import com.codepath.travel.adapters.OnItemListener;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.codepath.travel.CalendarUtils.days;
import static com.codepath.travel.CalendarUtils.formatDate;
import static com.codepath.travel.CalendarUtils.getDaysInMonth;
import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * A simple {@link Fragment} subclass.
 * This fragment displays the itinerary for the trip, with the date of the chosen flight already loaded in.
 * The tourist spots which the user chose for that location is also loaded in, and will automatically be
 * scheduled, but the user can edit the suggested itinerary.
 */
public class ItineraryFragment extends Fragment implements OnItemListener {

    private TextView tvMonthYear;
    private Button btnPreviousMonth;
    private Button btnNextMonth;
    private RecyclerView rvCalendar;
    private CalendarAdapter adapter;
    private Button btnWeeklyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        rvCalendar = view.findViewById(R.id.rvCalendar);
        btnWeeklyView = view.findViewById(R.id.btnWeeklyView);

        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });

        btnWeeklyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WeeklyViewActivity.class);
                startActivity(i);
            }
        });

        days = new ArrayList<>();
        selectedDate = LocalDate.now();
        adapter = new CalendarAdapter(getContext(), days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        rvCalendar.setLayoutManager(layoutManager);
        rvCalendar.setAdapter(adapter);

        setMonthView();
    }

    private void setMonthView() {
        tvMonthYear.setText(formatDate(selectedDate));
        getDaysInMonth(selectedDate);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.isEmpty()) {
            String message = "Selected Date " + dayText + " " + formatDate(selectedDate);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}