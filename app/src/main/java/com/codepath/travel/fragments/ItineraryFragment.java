package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.WeeklyViewActivity;
import com.codepath.travel.adapters.CalendarAdapter;
import com.codepath.travel.adapters.OnItemListener;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Event;
import com.codepath.travel.models.TouristDestination;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.codepath.travel.CalendarUtils.busyTimeSlots;
import static com.codepath.travel.CalendarUtils.datesOfInterest;
import static com.codepath.travel.CalendarUtils.days;
import static com.codepath.travel.CalendarUtils.destinationColorCode;
import static com.codepath.travel.CalendarUtils.formatDate;
import static com.codepath.travel.CalendarUtils.generateDestinationColorCode;
import static com.codepath.travel.CalendarUtils.getDaysInMonth;
import static com.codepath.travel.CalendarUtils.getLocalDate;
import static com.codepath.travel.CalendarUtils.inboundArrivalDates;
import static com.codepath.travel.CalendarUtils.nextAvailableDate;
import static com.codepath.travel.CalendarUtils.scheduleTheseEvents;
import static com.codepath.travel.CalendarUtils.selectedDate;
import static com.codepath.travel.CalendarUtils.selectedDestination;
import static com.codepath.travel.CalendarUtils.setUpTimeSlots;
import static com.codepath.travel.models.Event.eventsList;

/**
 * A simple {@link Fragment} subclass.
 * This fragment displays the itinerary for the trip, with the date of the chosen flight already loaded in.
 * The tourist spots which the user chose for that location is also loaded in, and will automatically be
 * scheduled, but the user can edit the suggested itinerary.
 */
public class ItineraryFragment extends Fragment implements OnItemListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ItineraryFragment";
    private static final String SEE_ALL_ITINERARIES = "ALL";
    private TextView tvMonthYear;
    private ImageButton btnPreviousMonth;
    private ImageButton btnNextMonth;
    private ImageButton btnToday;
    private RecyclerView rvCalendar;
    private CalendarAdapter adapter;
    private Button btnWeeklyView;
    private Spinner destinationSpinner;
    public static ArrayList<Destination> allDestinations;
    private ArrayList<TouristDestination> unscheduledEvents;
    private ArrayList<TouristDestination> scheduledEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Itinerary");

        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        rvCalendar = view.findViewById(R.id.rvCalendar);
        btnWeeklyView = view.findViewById(R.id.btnWeeklyView);
        destinationSpinner = view.findViewById(R.id.destination_spinner);
        btnToday = view.findViewById(R.id.btnToday);

        reloadEverything();

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
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (View) tvMonthYear, "to_week_transition");
                startActivity(i, options.toBundle());
            }
        });

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = LocalDate.now();
                setMonthView();
            }
        });

        days = new ArrayList<>();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        if (unscheduledEvents == null) {
            unscheduledEvents = new ArrayList<>();
        }
        if (scheduledEvents == null) {
            scheduledEvents = new ArrayList<>();
        }
        reloadAdapter();
        setMonthView();
    }

    private void reloadAdapter() {
        adapter = new CalendarAdapter(getContext(), days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        rvCalendar.setLayoutManager(layoutManager);
        rvCalendar.setAdapter(adapter);
    }

    private void reloadEverything() {
        eventsList.clear();
        destinationColorCode.clear();
        datesOfInterest.clear();
        busyTimeSlots.clear();
        nextAvailableDate.clear();
        loadAllDestinations();
    }

    private void loadAllDestinations() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.whereEqualTo(Destination.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> destinations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to load locations", e);
                    Toast.makeText(getContext(), "Unable to load locations", Toast.LENGTH_SHORT).show();
                    return;
                }
                allDestinations = (ArrayList) destinations;
                loadAllEvents();
                ArrayList<String> destinationNames = new ArrayList<>();
                destinationNames.add(SEE_ALL_ITINERARIES);
                for (Destination destination : destinations) {
                    destinationNames.add(destination.getFormattedLocationName());
                    datesOfInterest.put(destination.getDate(), destination);
                    inboundArrivalDates.put(destination, destination.getInboundDate());
                    String arrivalName;
                    Event event;
                    if (destination.getDate() != null) {
                        arrivalName = "Arrival at " + destination.getArriveAirportName() + " Airport in " + destination.getFormattedLocationName();
                        event = new Event(arrivalName, getLocalDate(destination.getDate()), LocalTime.of(12, 0, 0, 0), LocalTime.of(20, 0, 0, 0));
                        eventsList.add(event);
                    }
                    if (destination.getInboundDate() != null) {
                        arrivalName = "Arrival at " + destination.getInboundArriveName() + " Airport (Return from trip)";
                        event = new Event(arrivalName, getLocalDate(destination.getInboundDate()), LocalTime.of(12, 0, 0, 0), LocalTime.of(20, 0, 0, 0));
                        eventsList.add(event);
                    }
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner_destination, destinationNames);
                destinationSpinner.setAdapter(spinnerAdapter);
                destinationSpinner.setOnItemSelectedListener(ItineraryFragment.this);

                generateDestinationColorCode(allDestinations);
            }
        });
    }

    private void loadAllEvents() {
        ParseQuery<TouristDestination> query = ParseQuery.getQuery(TouristDestination.class);
        query.include(TouristDestination.KEY_DESTINATION);
        query.whereEqualTo(TouristDestination.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<TouristDestination>() {
            @Override
            public void done(List<TouristDestination> touristDestinations, ParseException e) {
                for (TouristDestination touristDestination : touristDestinations) {
                    if (touristDestination.getDateVisited() == null) {
                        unscheduledEvents.add(touristDestination);
                    } else {
                        scheduledEvents.add(touristDestination);
                    }
                }
                setUpTimeSlots(scheduledEvents);
                scheduleTheseEvents(unscheduledEvents);
                scheduledEvents.addAll(unscheduledEvents);
                unscheduledEvents.clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadAdapter();
        setMonthView();
    }

    private void setMonthView() {
        tvMonthYear.setText(formatDate(selectedDate));
        getDaysInMonth(selectedDate);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            selectedDate = date;
            setMonthView();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).equals(SEE_ALL_ITINERARIES)) {
            selectedDate = LocalDate.now();
            selectedDestination = null;
        } else {
            selectedDate = getLocalDate(allDestinations.get(position - 1).getDate());
            selectedDestination = allDestinations.get(position - 1);
        }
        setMonthView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}