package com.codepath.travel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.adapters.RoundtripsAdapter;
import com.codepath.travel.fragments.Ticket;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Flight;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;

public class RoundtripFlightsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "RoundtripFlightsActivity";
    private static final String[] sortMethods = {"", "Cost", "Departure Date", "Return Date", "Outbound Airline", "Inbound Airline"};
    private static CardView cvChosenFlight;
    private static Button btnConfirm;
    private static Flight chosenOutboundFlight;
    private static Flight chosenInboundFlight;
    private static Context context;
    private RecyclerView rvFlights;
    private ProgressBar pbFlights;
    private RoundtripsAdapter adapter;
    private Dictionary<Integer, String> placesCode;
    private Dictionary<Integer, String> placesName;
    private Dictionary<Integer, String> carriers;
    private ArrayList<Pair<Flight, Flight>> flights;
    private Spinner spinnerSortBy;

    public static void chooseRoundtrip(Pair<Flight, Flight> flightPair) {
        chosenOutboundFlight = flightPair.first;
        chosenInboundFlight = flightPair.second;
        btnConfirm.setClickable(true);
        btnConfirm.setBackgroundColor(context.getResources().getColor(R.color.pastel_pink));

        displayTicket(flightPair, cvChosenFlight);
        cvChosenFlight.setVisibility(View.VISIBLE);
    }

    public static void displayTicket(Pair<Flight, Flight> flightPair, View view) {
        Flight flightOut = flightPair.first;
        Flight flightIn = flightPair.second;

        ((TextView) view.findViewById(R.id.tvDepartAirport)).setText(flightOut.getDepartAirportName());
        ((TextView) view.findViewById(R.id.tvArriveAirport)).setText(flightOut.getArriveAirportName());
        String formattedCost = "$" + flightOut.getFlightCost();
        ((TextView) view.findViewById(R.id.tvCost)).setText(formattedCost);
        ((TextView) view.findViewById(R.id.tvAirline)).setText(flightOut.getCarrier());
        ((TextView) view.findViewById(R.id.tvDate)).setText(flightOut.getDate());

        ((TextView) view.findViewById(R.id.tvReturnDepartAirport)).setText(flightIn.getDepartAirportName());
        ((TextView) view.findViewById(R.id.tvReturnArriveAirport)).setText(flightIn.getArriveAirportName());
        ((TextView) view.findViewById(R.id.tvReturnAirline)).setText(flightIn.getCarrier());
        ((TextView) view.findViewById(R.id.tvReturnDate)).setText(flightIn.getDate());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roundtrip_flights);
        context = this;

        cvChosenFlight = findViewById(R.id.cvChosenFlight);
        pbFlights = findViewById(R.id.pbFlights);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra(getString(R.string.outbound), Parcels.wrap(chosenOutboundFlight));
                intent.putExtra(getString(R.string.inbound), Parcels.wrap(chosenInboundFlight));
                finish();
            }
        });

        placesCode = new Hashtable<>();
        placesName = new Hashtable<>();
        carriers = new Hashtable<>();
        flights = new ArrayList<>();

        rvFlights = findViewById(R.id.rvFlights);
        adapter = new RoundtripsAdapter(this, flights);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(adapter);

        pbFlights.setVisibility(View.VISIBLE);
        for (Airport originAirport : FlightsActivity.departureAirports) {
            for (Airport destinationAirport : FlightsActivity.arrivalAirports) {
                Ticket.getRoundtripFlights(originAirport.getIATACode(), destinationAirport.getIATACode(), this, pbFlights,
                        adapter, TAG, this, placesCode, placesName, carriers, flights);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortMethods);
        spinnerSortBy.setAdapter(adapter);
        spinnerSortBy.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (sortMethods[position]) {
            case "Cost":
                Collections.sort(flights, new Comparator<Pair<Flight, Flight>>() {
                    @Override
                    public int compare(Pair<Flight, Flight> o1, Pair<Flight, Flight> o2) {
                        return Ticket.compareCost.compare(o1.first, o2.first);
                    }
                });
                break;
            case "Departure Date":
                Collections.sort(flights, new Comparator<Pair<Flight, Flight>>() {
                    @Override
                    public int compare(Pair<Flight, Flight> o1, Pair<Flight, Flight> o2) {
                        return Ticket.compareDate.compare(o1.first, o2.first);
                    }
                });
                break;
            case "Return Date":
                Collections.sort(flights, new Comparator<Pair<Flight, Flight>>() {
                    @Override
                    public int compare(Pair<Flight, Flight> o1, Pair<Flight, Flight> o2) {
                        return Ticket.compareDate.compare(o1.second, o2.second);
                    }
                });
                break;
            case "Outbound Airline":
                Collections.sort(flights, new Comparator<Pair<Flight, Flight>>() {
                    @Override
                    public int compare(Pair<Flight, Flight> o1, Pair<Flight, Flight> o2) {
                        return Ticket.compareAirline.compare(o1.first, o2.first);
                    }
                });
                break;
            case "Inbound Airline":
                Collections.sort(flights, new Comparator<Pair<Flight, Flight>>() {
                    @Override
                    public int compare(Pair<Flight, Flight> o1, Pair<Flight, Flight> o2) {
                        return Ticket.compareAirline.compare(o1.second, o2.second);
                    }
                });
                break;
        }
        adapter.notifyDataSetChanged();
        rvFlights.smoothScrollToPosition(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}