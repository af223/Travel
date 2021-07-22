package com.codepath.travel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.FlightsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Flight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Headers;

/**
 * This activity allows the user to see all flights from their chosen departure airports landing in
 * their chosen arrival airports. The user can choose a ticket by clicking on it, then clicking the confirm
 * button, at which point they're directed back to FlightsActivity.java and the data is stored on Parse.
 * <p>
 * This activity appears when the user clicks the "see flights" buttons from FlightsActivity.java.
 */

public class ChooseFlightActivity extends AppCompatActivity {

    private static final String TAG = "ChooseFlightActivity";
    private static final String getRoutesURLBase = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/%1$s/%2$s/anytime?inboundpartialdate=anytime";
    private static CardView cvFlight;
    private static Button btnConfirm;
    private static TextView tvDepartAirport;
    private static TextView tvArriveAirport;
    private static TextView tvCost;
    private static TextView tvAirline;
    private static TextView tvDate;
    private static Context context;
    private static Flight chosenFlight;
    private RecyclerView rvFlights;
    private ProgressBar pbFlights;
    private ArrayList<Flight> flights;
    private FlightsAdapter adapter;
    private Dictionary<Integer, String> placesCode;
    private Dictionary<Integer, String> placesName;
    private Dictionary<Integer, String> carriers;

    // User has selected a ticket, but not clicked confirm yet
    public static void choose(Flight flight) {
        chosenFlight = flight;
        tvDepartAirport.setText(flight.getDepartAirportName());
        tvArriveAirport.setText(flight.getArriveAirportName());
        String formattedCost = "$" + flight.getFlightCost();
        tvCost.setText(formattedCost);
        tvAirline.setText(flight.getCarrier());
        tvDate.setText(flight.getDate());
        cvFlight.setVisibility(View.VISIBLE);
        btnConfirm.setClickable(true);
        btnConfirm.setBackgroundColor(getContext().getResources().getColor(R.color.purple_500));
    }

    private static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flight);

        context = this;
        cvFlight = findViewById(R.id.card_view);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvDepartAirport = findViewById(R.id.tvDepartAirport);
        tvArriveAirport = findViewById(R.id.tvArriveAirport);
        tvCost = findViewById(R.id.tvCost);
        tvAirline = findViewById(R.id.tvAirline);
        tvDate = findViewById(R.id.tvDate);
        pbFlights = findViewById(R.id.pbFlights);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra(Flight.class.getSimpleName(), Parcels.wrap(chosenFlight));
                finish();
            }
        });

        placesCode = new Hashtable<>();
        placesName = new Hashtable<>();
        carriers = new Hashtable<>();
        flights = new ArrayList<>();

        rvFlights = findViewById(R.id.rvFlights);
        adapter = new FlightsAdapter(this, flights);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(adapter);

        pbFlights.setVisibility(View.VISIBLE);
        for (Airport originAirport : FlightsActivity.departureAirports) {
            for (Airport destinationAirport : FlightsActivity.arrivalAirports) {
                getFlights(originAirport.getIATACode(), destinationAirport.getIATACode());
            }
        }
    }

    private void getFlights(String originCode, String destinationCode) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        client.get(String.format(getRoutesURLBase, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            processPlaces(json.jsonObject.getJSONArray("Places"));
                            processCarriers(json.jsonObject.getJSONArray("Carriers"));
                            processFlights(json.jsonObject.getJSONArray("Quotes"));
                            pbFlights.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e(TAG, "unable to parse response", e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Flights request failed: ", throwable);
                        Toast.makeText(ChooseFlightActivity.this, "Unable to get flights", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processPlaces(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).has("IataCode")) {
                    placesCode.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("IataCode"));
                } else {
                    placesCode.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("SkyscannerCode"));
                }
                placesName.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("Name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to process places", e);
            e.printStackTrace();
        }
    }

    private void processCarriers(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                carriers.put(jsonArray.getJSONObject(i).getInt("CarrierId"), jsonArray.getJSONObject(i).getString("Name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to process carriers", e);
            e.printStackTrace();
        }
    }

    private void processFlights(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);
                Boolean isDirect = flightObject.getBoolean("Direct");
                String cost = String.valueOf(flightObject.getInt("MinPrice"));
                JSONArray carrierIds = flightObject.getJSONObject("OutboundLeg").getJSONArray("CarrierIds");
                String carrier = carriers.get(carrierIds.get(0));
                Integer originId = flightObject.getJSONObject("OutboundLeg").getInt("OriginId");
                String departAirportName = placesName.get(originId);
                String departAirportCode = placesCode.get(originId);
                Integer destinationId = flightObject.getJSONObject("OutboundLeg").getInt("DestinationId");
                String arriveAirportName = placesName.get(destinationId);
                String arriveAirportCode = placesCode.get(destinationId);
                String date = flightObject.getJSONObject("OutboundLeg").getString("DepartureDate").substring(0, 10);
                Flight flight = new Flight(departAirportCode, departAirportName, arriveAirportCode,
                        arriveAirportName, cost, carrier, date, isDirect);
                flights.add(flight);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to processFlights", e);
            e.printStackTrace();
        }
    }
}