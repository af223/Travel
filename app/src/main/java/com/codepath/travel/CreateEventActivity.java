package com.codepath.travel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.travel.fragments.ItineraryFragment;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Event;
import com.codepath.travel.models.TouristDestination;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * This activity allows the user to manually create an event to add to the schedule in weekly view.
 * <p>
 * This activity is launched from WeeklyViewActivity when the user clicks the '+' button, and if the user
 * clicks "save event", the user is automatically directed back to WeeklyViewActivity with the new event
 * added on the schedule.
 */

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText etEventName;
    private Button btnSelectDate;
    private static int hourStart, minuteStart, hourEnd, minuteEnd, year, month, day;
    private LocalTime time;
    private LocalTime endTime;
    private Button btnCreateEvent;
    private static final Calendar calendar = Calendar.getInstance();;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialogStart;
    private TimePickerDialog timePickerDialogEnd;
    private Button btnSelectTime;
    private Button btnSelectEndTime;
    private LocalDate chosenEventDate;
    private Spinner destinationSpinner;
    private CheckBox cbDestination;
    private Destination associatedDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setTimesToToday();
        createPickerDialogs();

        etEventName = findViewById(R.id.etEventName);
        cbDestination = findViewById(R.id.cbDestination);
        cbDestination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    destinationSpinner.setVisibility(View.VISIBLE);
                } else {
                    destinationSpinner.setVisibility(View.GONE);
                    associatedDestination = null;
                }
            }
        });
        destinationSpinner = findViewById(R.id.destination_spinner);
        ArrayList<String> destinationNames = new ArrayList<>();
        for (Destination destination : ItineraryFragment.allDestinations) {
            destinationNames.add(destination.getFormattedLocationName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_destination, destinationNames);
        destinationSpinner.setAdapter(spinnerAdapter);
        destinationSpinner.setOnItemSelectedListener(this);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectDate.setText(formatDateString(month, day, year));
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSelectTime.setText(CalendarUtils.formatTime(time));
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogStart.show();
            }
        });
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnSelectEndTime.setText(CalendarUtils.formatTime(time.plusHours(2)));
        btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogEnd.show();
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
                if (endTime.isBefore(time)) {
                    Toast.makeText(CreateEventActivity.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }
                String eventName = etEventName.getText().toString();
                Event newEvent = new Event(eventName, chosenEventDate, time, endTime);
                Event.eventsList.add(newEvent);
                saveCreatedEvent();
            }
        });
    }

    private void setTimesToToday() {
        chosenEventDate = selectedDate;
        time = LocalTime.now();
        endTime = time.plusHours(2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hourStart = time.getHour();
        minuteStart = time.getMinute();
        hourEnd = endTime.getHour();
        minuteEnd = endTime.getMinute();
    }

    private void createPickerDialogs() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int pickedYear, int pickedMonth, int pickedDay) {
                year = pickedYear;
                month = pickedMonth;
                day = pickedDay;
                String date = formatDateString(month, day, year);
                btnSelectDate.setText(date);
                chosenEventDate = LocalDate.of(year, month+1, day);
            }
        };
        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hourStart = hour;
                minuteStart = minute;
                time = LocalTime.of(hourStart, minuteStart);
                btnSelectTime.setText(CalendarUtils.formatTime(time));
            }
        };
        timePickerDialogStart = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, onTimeSetListener, hourStart, minuteStart, false);
        TimePickerDialog.OnTimeSetListener onTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hourEnd = hour;
                minuteEnd = minute;
                endTime = LocalTime.of(hourEnd, minuteEnd);
                btnSelectEndTime.setText(CalendarUtils.formatTime(endTime));
            }
        };
        timePickerDialogEnd = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, onTimeSetListenerEnd, hourEnd, minuteEnd, false);
    }

    private String formatDateString(int month, int dayOfMonth, int year) {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        return formatter.format(calendar.getTime());
    }

    private void saveCreatedEvent() {
        TouristDestination createdEvent = new TouristDestination();
        createdEvent.setUser(ParseUser.getCurrentUser());
        if (associatedDestination != null) {
            createdEvent.setDestination(associatedDestination);
        }
        createdEvent.setPlaceId("N/A");
        createdEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateEventActivity.this, "Unable to save location", Toast.LENGTH_SHORT).show();
                    Log.e("CreateEvent", e.toString());
                    return;
                }
                Toast.makeText(CreateEventActivity.this, "Event saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        associatedDestination = ItineraryFragment.allDestinations.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        associatedDestination = null;
    }
}