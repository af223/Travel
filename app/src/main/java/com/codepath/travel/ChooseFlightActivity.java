package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.FlightsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Flight;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Headers;

public class ChooseFlightActivity extends AppCompatActivity {

    private static final String TAG = "ChooseFlightActivity";
    private RecyclerView rvFlights;
    private static String getRoutesURLBase = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/%1$s/%2$s/anytime?inboundpartialdate=anytime";
    private ArrayList<Flight> flights;
    private FlightsAdapter adapter;
    private Dictionary<Integer, String> placesCode;
    private Dictionary<Integer, String> placesName;
    private Dictionary<Integer, String> carriers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flight);

        placesCode = new Hashtable<>();
        placesName = new Hashtable<>();
        carriers = new Hashtable<>();
        flights = new ArrayList<>();

        rvFlights = findViewById(R.id.rvFlights);
        adapter = new FlightsAdapter(this, flights);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(adapter);

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
                String arriveAirportCode = placesCode.get(originId);
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

    private void processPlaces(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                placesCode.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("SkyscannerCode"));
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
}