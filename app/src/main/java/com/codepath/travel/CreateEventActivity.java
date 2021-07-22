package com.codepath.travel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.travel.models.Event;

import java.time.LocalTime;

import static com.codepath.travel.CalendarUtils.formatDateForEvent;
import static com.codepath.travel.CalendarUtils.formatTime;
import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * This activity allows the user to manually create an event to add to the schedule in weekly view.
 * TODO: implement popup time and date clicker
 */

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventName;
    private TextView tvEventDate, tvEventTime;
    private LocalTime time;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etEventName = findViewById(R.id.etEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEventName.getText().toString().isEmpty()) {
                    Toast.makeText(CreateEventActivity.this, "Must name event", Toast.LENGTH_SHORT).show();
                    return;
                }
                String eventName = etEventName.getText().toString();
                Event newEvent = new Event(eventName, selectedDate, time);
                Event.eventsList.add(newEvent);
                finish();
            }
        });

        time = LocalTime.now();
        tvEventDate.setText("Date: " + formatDateForEvent(selectedDate));
        tvEventTime.setText("Time: " + formatTime(time));
    }
}