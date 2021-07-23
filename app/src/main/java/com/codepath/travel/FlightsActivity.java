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
 * and select the flight. The user can choose to see a roundtrip or one-way tickets.
 * <p>
 * This activity is started when the user chooses "Flights" in ResourcesFragment.java.
 */

public class FlightsActivity extends AppCompatActivity {

    private static final int CHOOSE_ONE_WAY_FLIGHT_REQUEST_CODE = 13;
    private static final int CHOOSE_ROUND_FLIGHT_REQUEST_CODE = 9;
    private static final String TAG = "FlightsActivity";
    public static ArrayList<Airport> departureAirports = new ArrayList<>();
    public static ArrayList<Airport> arrivalAirports = new ArrayList<>();
    private Button btnDepart;
    private Button btnArrive;
    private Button btnToOneWay;
    private Button btnToRoundTrips;
    private Toolbar toolbar;
    private Destination thisDestination;
    private View chosenOutboundFlight;
    private View chosenInboundFlight;
    private TextView tvFlightType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Flights");

        chosenOutboundFlight = findViewById(R.id.chosenOutboundFlight);
        chosenInboundFlight = findViewById(R.id.chosenInboundFlight);
        btnDepart = findViewById(R.id.btnDepart);
        btnArrive = findViewById(R.id.btnArrive);
        btnToOneWay = findViewById(R.id.btnToOneWay);
        btnToRoundTrips = findViewById(R.id.btnToRoundTrips);
        tvFlightType = findViewById(R.id.tvFlightType);

        departureAirports.clear();
        arrivalAirports.clear();
        getChosenDestination();

        btnToOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAirportsSelected()) {
                    Intent i = new Intent(FlightsActivity.this, ChooseFlightActivity.class);
                    i.putExtra(getString(R.string.flight_type), CHOOSE_ONE_WAY_FLIGHT_REQUEST_CODE);
                    startActivityForResult(i, CHOOSE_ONE_WAY_FLIGHT_REQUEST_CODE);
                }
            }
        });

        btnToRoundTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAirportsSelected()) {
                    Intent i = new Intent(FlightsActivity.this, RoundtripFlightsActivity.class);
                    i.putExtra(getString(R.string.flight_type), CHOOSE_ROUND_FLIGHT_REQUEST_CODE);
                    startActivityForResult(i, CHOOSE_ROUND_FLIGHT_REQUEST_CODE);
                }
            }
        });
    }

    private boolean checkAirportsSelected() {
        if (departureAirports.isEmpty()) {
            Toast.makeText(FlightsActivity.this, "Need to select at least one departure airport", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (arrivalAirports.isEmpty()) {
            Toast.makeText(FlightsActivity.this, "Need to select at least one destination airport", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                loadChosenFlights();
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

    private void loadChosenFlights() {
        if (thisDestination.getDate() == null) {
            chosenOutboundFlight.setVisibility(View.INVISIBLE);
        } else {
            setFlightType();
            chosenOutboundFlight.setVisibility(View.VISIBLE);
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvDepartAirport)).setText(thisDestination.getDepartAirportName());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvArriveAirport)).setText(thisDestination.getArriveAirportName());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvCost)).setText("$" + thisDestination.getCost());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvAirline)).setText(thisDestination.getCarrier());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvDate)).setText(thisDestination.getDate());
        }
        if (thisDestination.getInboundDate() == null) {
            chosenInboundFlight.setVisibility(View.INVISIBLE);
        } else {
            setFlightType();
            chosenInboundFlight.setVisibility(View.VISIBLE);
            ((TextView) chosenInboundFlight.findViewById(R.id.tvDepartAirport)).setText(thisDestination.getInboundDepartName());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvArriveAirport)).setText(thisDestination.getInboundArriveName());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvCost)).setText("$" + thisDestination.getInboundCost());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvAirline)).setText(thisDestination.getInboundCarrier());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvDate)).setText(thisDestination.getInboundDate());
        }
    }

    private void setFlightType() {
        if (thisDestination.isRoundtrip()) {
            tvFlightType.setText(R.string.is_roundtrip);
            chosenInboundFlight.findViewById(R.id.tvCost).setVisibility(View.INVISIBLE);
        } else {
            tvFlightType.setText(R.string.is_one_way);
            chosenInboundFlight.findViewById(R.id.tvCost).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_ONE_WAY_FLIGHT_REQUEST_CODE) {
                if (data.hasExtra(getString(R.string.outbound))) {
                    Flight chosenOutFlight = Parcels.unwrap(data.getParcelableExtra(getString(R.string.outbound)));
                    saveOutboundFlightData(chosenOutFlight);
                }
                if (data.hasExtra(getString(R.string.inbound))) {
                    Flight chosenInFlight = Parcels.unwrap(data.getParcelableExtra(getString(R.string.inbound)));
                    saveInboundFlightData(chosenInFlight);
                }

            } else if (requestCode == CHOOSE_ROUND_FLIGHT_REQUEST_CODE) {
                Flight chosenOutFlight = Parcels.unwrap(data.getParcelableExtra(getString(R.string.outbound)));
                saveOutboundFlightData(chosenOutFlight);
                Flight chosenInFlight = Parcels.unwrap(data.getParcelableExtra(getString(R.string.inbound)));
                saveInboundFlightData(chosenInFlight);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveOutboundFlightData(Flight chosenFlight) {
        thisDestination.setDepartAirportCode(chosenFlight.getDepartAirportCode());
        thisDestination.setDepartAirportName(chosenFlight.getDepartAirportName());
        thisDestination.setArriveAirportCode(chosenFlight.getArriveAirportCode());
        thisDestination.setArriveAirportName(chosenFlight.getArriveAirportName());
        thisDestination.setCost(chosenFlight.getFlightCost());
        thisDestination.setCarrier(chosenFlight.getCarrier());
        thisDestination.setDate(chosenFlight.getDate());
        if (thisDestination.isRoundtrip() != null && thisDestination.isRoundtrip() && !chosenFlight.isRoundtrip()) {
            clearInboundFlightData();
        }
        thisDestination.setIsRoundtrip(chosenFlight.isRoundtrip());
        thisDestination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving flight: ", e);
                    Toast.makeText(FlightsActivity.this, "Error while saving flight", Toast.LENGTH_SHORT).show();
                } else {
                    loadChosenFlights();
                    Toast.makeText(FlightsActivity.this, "flight chosen!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearInboundFlightData() {
        thisDestination.remove(Destination.KEY_INBOUND_DEPART_CODE);
        thisDestination.remove(Destination.KEY_INBOUND_DEPART_NAME);
        thisDestination.remove(Destination.KEY_INBOUND_ARRIVE_CODE);
        thisDestination.remove(Destination.KEY_INBOUND_ARRIVE_NAME);
        thisDestination.remove(Destination.KEY_INBOUND_COST);
        thisDestination.remove(Destination.KEY_INBOUND_CARRIER);
        thisDestination.remove(Destination.KEY_INBOUND_DATE);
    }

    private void saveInboundFlightData(Flight chosenFlight) {
        thisDestination.setInboundDepartCode(chosenFlight.getDepartAirportCode());
        thisDestination.setInboundDepartName(chosenFlight.getDepartAirportName());
        thisDestination.setInboundArriveCode(chosenFlight.getArriveAirportCode());
        thisDestination.setInboundArriveName(chosenFlight.getArriveAirportName());
        thisDestination.setInboundCost(chosenFlight.getFlightCost());
        thisDestination.setInboundCarrier(chosenFlight.getCarrier());
        thisDestination.setInboundDate(chosenFlight.getDate());
        if (thisDestination.isRoundtrip() != null && thisDestination.isRoundtrip() && !chosenFlight.isRoundtrip()) {
            clearOutboundFlightData();
        }
        thisDestination.setIsRoundtrip(chosenFlight.isRoundtrip());
        thisDestination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving flight: ", e);
                    Toast.makeText(FlightsActivity.this, "Error while saving flight", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadChosenFlights();
                Toast.makeText(FlightsActivity.this, "flight chosen!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearOutboundFlightData() {
        thisDestination.remove(Destination.KEY_DEPART_CODE);
        thisDestination.remove(Destination.KEY_DEPART_NAME);
        thisDestination.remove(Destination.KEY_ARRIVE_NAME);
        thisDestination.remove(Destination.KEY_ARRIVE_CODE);
        thisDestination.remove(Destination.KEY_COST);
        thisDestination.remove(Destination.KEY_CARRIER);
        thisDestination.remove(Destination.KEY_DATE);
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