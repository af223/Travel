package com.codepath.travel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.travel.models.Event;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * This activity allows the user to manually create an event to add to the schedule in weekly view.
 * TODO: implement popup time and date clicker
 */

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventName;
    private Button btnSelectDate;
    private LocalTime time;
    private Button btnCreateEvent;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private LocalDate chosenEventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        chosenEventDate = selectedDate;
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = formatDateString(month, dayOfMonth, year);
                btnSelectDate.setText(date);
                chosenEventDate = LocalDate.of(year, month, dayOfMonth);
            }
        };

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        time = LocalTime.now();
        etEventName = findViewById(R.id.etEventName);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectDate.setText(formatDateString(month, dayOfMonth, year));
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEventName.getText().toString().isEmpty()) {
                    Toast.makeText(CreateEventActivity.this, "Must name event", Toast.LENGTH_SHORT).show();
                    return;
                }
                String eventName = etEventName.getText().toString();
                Event newEvent = new Event(eventName, chosenEventDate, time, time.plusHours(2));
                Event.eventsList.add(newEvent);
                finish();
            }
        });

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, dayOfMonth);
    }

    private String formatDateString(int month, int dayOfMonth, int year) {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}