package com.codepath.travel.activities;

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

import com.codepath.travel.CalendarUtils;
import com.codepath.travel.R;
import com.codepath.travel.fragments.ItineraryFragment;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Event;
import com.codepath.travel.models.TouristDestination;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import static com.codepath.travel.CalendarUtils.formatStoredTime;
import static com.codepath.travel.CalendarUtils.selectedDate;

/**
 * This activity allows the user to manually create an event to add to the schedule in weekly view.
 * <p>
 * This activity is launched from WeeklyViewActivity when the user clicks the '+' button, and if the user
 * clicks "save event", the user is automatically directed back to WeeklyViewActivity with the new event
 * added on the schedule.
 */

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CreateEventActivity";
    private static final Calendar calendar = Calendar.getInstance();
    private static int hourStart, minuteStart, hourEnd, minuteEnd, year, month, day;
    private EditText etEventName;
    private Button btnSelectDate;
    private LocalTime time;
    private LocalTime endTime;
    private Button btnCreateEvent;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialogStart;
    private TimePickerDialog timePickerDialogEnd;
    private Button btnSelectTime;
    private Button btnSelectEndTime;
    private LocalDate chosenEventDate;
    private Spinner destinationSpinner;
    private CheckBox cbDestination;
    private Destination associatedDestination;
    private String eventName;
    private Button btnDeleteEvent;
    private Event currentEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        cbDestination = findViewById(R.id.cbDestination);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
        etEventName = findViewById(R.id.etEventName);
        destinationSpinner = findViewById(R.id.destination_spinner);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        if (getIntent().getIntExtra(getResources().getString(R.string.edit_event_intent), 0) == 1) {
            cbDestination.setVisibility(View.GONE);
            currentEvent = Event.eventsList.get(getIntent().getIntExtra(getResources().getString(R.string.event), 0));
            etEventName.setText(currentEvent.getName());
            setTimes(currentEvent.getTime(), currentEvent.getEndTime());
            btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasInvalidFields())
                        return;
                    editEvent();
                }
            });
            btnDeleteEvent.setVisibility(View.VISIBLE);
            btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEvent();
                }
            });
        } else {
            setTimes(LocalTime.now(), LocalTime.now().plusHours(2));
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
            ArrayList<String> destinationNames = new ArrayList<>();
            for (Destination destination : ItineraryFragment.allDestinations) {
                destinationNames.add(destination.getFormattedLocationName());
            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_destination, destinationNames);
            destinationSpinner.setAdapter(spinnerAdapter);
            destinationSpinner.setOnItemSelectedListener(this);
            btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasInvalidFields())
                        return;
                    eventName = etEventName.getText().toString();
                    Event newEvent = new Event(eventName, chosenEventDate, time, endTime, true);
                    Event.eventsList.add(newEvent);
                    saveCreatedEvent(newEvent);
                }
            });
        }
        createPickerDialogs();

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
        btnSelectEndTime.setText(CalendarUtils.formatTime(endTime));
        btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogEnd.show();
            }
        });
    }

    private void deleteEvent() {
        ParseQuery<TouristDestination> query = ParseQuery.getQuery(TouristDestination.class);
        query.getInBackground(currentEvent.getTouristDestinationId(), new GetCallback<TouristDestination>() {
            @Override
            public void done(TouristDestination object, ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateEventActivity.this, "Unable to delete event", Toast.LENGTH_SHORT).show();
                    return;
                }
                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(CreateEventActivity.this, "Unable to delete event", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Event.eventsList.remove(getIntent().getIntExtra(getResources().getString(R.string.event), 0));
                        finish();
                    }
                });
            }
        });
    }

    private void editEvent() {
        eventName = etEventName.getText().toString();
        if (!eventName.equals(currentEvent.getName())
                || !time.equals(currentEvent.getTime())
                || !endTime.equals(currentEvent.getEndTime())
                || !chosenEventDate.equals(currentEvent.getDate())) {
            ParseQuery<TouristDestination> query = ParseQuery.getQuery(TouristDestination.class);
            query.getInBackground(currentEvent.getTouristDestinationId(), new GetCallback<TouristDestination>() {
                @Override
                public void done(TouristDestination touristDestination, ParseException e) {
                    if (e != null) {
                        Toast.makeText(CreateEventActivity.this, "Unable to save event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateFields(touristDestination);
                    currentEvent.setName(eventName);
                    currentEvent.setTime(time);
                    currentEvent.setEndTime(endTime);
                    currentEvent.setDate(chosenEventDate);
                    touristDestination.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(CreateEventActivity.this, "Unable to save event", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(CreateEventActivity.this, "Event updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        }
    }

    private Boolean hasInvalidFields() {
        if (etEventName.getText().toString().isEmpty()) {
            Toast.makeText(CreateEventActivity.this, "Must name event", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (endTime.isBefore(time)) {
            Toast.makeText(CreateEventActivity.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void setTimes(LocalTime startTime, LocalTime eventEndTime) {
        chosenEventDate = selectedDate;
        time = startTime;
        endTime = eventEndTime;
        calendar.setTime(Date.valueOf(selectedDate.toString()));
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
                chosenEventDate = LocalDate.of(year, month + 1, day);
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

    private void saveCreatedEvent(Event newEvent) {
        TouristDestination createdEvent = new TouristDestination();
        createdEvent.setUser(ParseUser.getCurrentUser());
        if (associatedDestination != null) {
            createdEvent.setDestination(associatedDestination);
        }
        createdEvent.setPlaceId("N/A");
        updateFields(createdEvent);
        createdEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateEventActivity.this, "Unable to save event", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                    return;
                }
                Toast.makeText(CreateEventActivity.this, "Event saved!", Toast.LENGTH_SHORT).show();
                newEvent.setTouristDestinationId(createdEvent.getObjectId());
                finish();
            }
        });
    }

    private void updateFields(TouristDestination touristDestination) {
        touristDestination.setName(eventName);
        touristDestination.setDateVisited(chosenEventDate.toString());
        touristDestination.setTimeVisited(formatStoredTime(time));
        touristDestination.setVisitEnd(formatStoredTime(endTime));
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