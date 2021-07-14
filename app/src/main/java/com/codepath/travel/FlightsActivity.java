package com.codepath.travel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Destination;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

import okhttp3.Headers;

public class FlightsActivity extends AppCompatActivity {

    private static final String TAG = "FlightsActivity";
    private static final int DEPARTURE_AIRPORT_REQUEST_CODE = 24;
    private static final int ARRIVAL_AIRPORT_REQUEST_CODE = 88;
    public static ArrayList<Airport> departureAirports = new ArrayList<>();
    public static ArrayList<Airport> arrivalAirports = new ArrayList<>();
    private TextView tvDepartureAirport;
    private TextView tvArrivalAirport;
    private Button btnDepart;
    private Button btnArrive;
    private Button btnToFlights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        tvDepartureAirport = findViewById(R.id.tvDepartureAirport);
        tvArrivalAirport = findViewById(R.id.tvArrivalAirport);
        btnDepart = findViewById(R.id.btnDepart);
        btnArrive = findViewById(R.id.btnArrive);
        btnToFlights = findViewById(R.id.btnFlights);

        getChosenDestination();
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
                        startActivityForResult(i, DEPARTURE_AIRPORT_REQUEST_CODE);
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
                        startActivityForResult(i, ARRIVAL_AIRPORT_REQUEST_CODE);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DEPARTURE_AIRPORT_REQUEST_CODE) {
                Toast.makeText(FlightsActivity.this, "from departure!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == ARRIVAL_AIRPORT_REQUEST_CODE) {
                Toast.makeText(FlightsActivity.this, "From arrival!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFlights() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        //client.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/SFO-sky/ORD-sky/anytime?inboundpartialdate=anytime", //query routes
        client.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query=Czechia", //query places
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("Costs", "worked");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("Costs", "failed");
                    }
                });
    }
}