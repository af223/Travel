package com.codepath.travel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Flight;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.codepath.travel.MainActivity.logout;

/**
 * This activity allows the user navigate to find and select departure and arrival airports and to see
 * and select the flight.
 * <p>
 * This activity is started when the user chooses "Flights" in ResourcesFragment.java.
 */

public class FlightsActivity extends AppCompatActivity {

    private static final int CHOOSE_FLIGHT_REQUEST_CODE = 24;
    private static final String TAG = "FlightsActivity";
    public static ArrayList<Airport> departureAirports = new ArrayList<>();
    public static ArrayList<Airport> arrivalAirports = new ArrayList<>();
    private TextView tvDepartureAirport;
    private TextView tvArrivalAirport;
    private Button btnDepart;
    private Button btnArrive;
    private Button btnToFlights;
    private Toolbar toolbar;
    private Destination thisDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Flights");

        tvDepartureAirport = findViewById(R.id.tvDepartureAirport);
        tvArrivalAirport = findViewById(R.id.tvArrivalAirport);
        btnDepart = findViewById(R.id.btnDepart);
        btnArrive = findViewById(R.id.btnArrive);
        btnToFlights = findViewById(R.id.btnToFlights);

        departureAirports.clear();
        arrivalAirports.clear();
        getChosenDestination();

        btnToFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (departureAirports.isEmpty()) {
                    Toast.makeText(FlightsActivity.this, "Need to select at least one departure airport", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (arrivalAirports.isEmpty()) {
                    Toast.makeText(FlightsActivity.this, "Need to select at least one destination airport", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(FlightsActivity.this, ChooseFlightActivity.class);
                startActivityForResult(i, CHOOSE_FLIGHT_REQUEST_CODE);
            }
        });
    }

    private void getChosenDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.getInBackground(getIntent().getStringExtra(Destination.KEY_OBJECT_ID), new GetCallback<Destination>() {
            @Override
            public void done(Destination destination, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to load destination", e);
                    Toast.makeText(FlightsActivity.this, "Unable to load destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                thisDestination = destination;
                if (destination.getDepartAirportName() != null) {
                    tvDepartureAirport.setText(destination.getDepartAirportName());
                    btnDepart.setText(getResources().getString(R.string.change_depart_airport));
                }
                if (destination.getArriveAirportName() != null) {
                    tvArrivalAirport.setText(destination.getArriveAirportName());
                    btnArrive.setText(getResources().getString(R.string.change_arrive_airport));
                }
                btnDepart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FlightsActivity.this, AirportSearchActivity.class);
                        i.putExtra(getResources().getString(R.string.from_departure), true);
                        startActivity(i);
                    }
                });
                btnArrive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FlightsActivity.this, AirportSearchActivity.class);
                        i.putExtra(getResources().getString(R.string.from_departure), false);
                        i.putExtra(Destination.KEY_LOCAL, destination.getLocality());
                        i.putExtra(Destination.KEY_ADMIN1, destination.getAdminArea1());
                        i.putExtra(Destination.KEY_COUNTRY, destination.getCountry());
                        startActivity(i);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_FLIGHT_REQUEST_CODE) {
                Flight chosenFlight = Parcels.unwrap(data.getParcelableExtra(Flight.class.getSimpleName()));
                saveFlightData(chosenFlight);
                tvDepartureAirport.setText(chosenFlight.getDepartAirportName());
                tvArrivalAirport.setText(chosenFlight.getArriveAirportName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveFlightData(Flight chosenFlight) {
        thisDestination.setDepartAirportCode(chosenFlight.getDepartAirportCode());
        thisDestination.setDepartAirportName(chosenFlight.getDepartAirportName());
        thisDestination.setArriveAirportCode(chosenFlight.getArriveAirportCode());
        thisDestination.setArriveAirportName(chosenFlight.getArriveAirportName());
        thisDestination.setCost(chosenFlight.getFlightCost());
        thisDestination.setCarrier(chosenFlight.getCarrier());
        thisDestination.setDate(chosenFlight.getDate());
        thisDestination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving flight: ", e);
                    Toast.makeText(FlightsActivity.this, "Error while saving flight", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FlightsActivity.this, "flight chosen!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout(this);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}