package com.codepath.travel.fragments;

import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.CalendarAdapter;
import com.codepath.travel.adapters.OnItemListener;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    private LocalDate selectedDate;
    private ArrayList<String> daysInMonth;
    private CalendarAdapter adapter;

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

        daysInMonth = new ArrayList<>();
        selectedDate = LocalDate.now();
        adapter = new CalendarAdapter(getContext(), daysInMonth, this);
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

    private void getDaysInMonth(LocalDate date) {
        daysInMonth.clear();
        YearMonth yearMonth = YearMonth.from(date);
        int numDaysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday is leftmost column

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > numDaysInMonth + dayOfWeek) {
                daysInMonth.add("");
            } else {
                daysInMonth.add(String.valueOf(i - dayOfWeek));
            }
        }
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.isEmpty()) {
            String message = "Selected Date " + dayText + " " + formatDate(selectedDate);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}