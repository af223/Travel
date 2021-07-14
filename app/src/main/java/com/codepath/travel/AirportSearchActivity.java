package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.ChosenAirportsAdapter;
import com.codepath.travel.adapters.FindAirportsAdapter;
import com.codepath.travel.models.Airport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

public class AirportSearchActivity extends AppCompatActivity {

    private static final String TAG = "AirportSearchActivity";
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvChosenAirports;
    private RecyclerView rvFindAirport;
    private Button btnClearChosen;
    private ArrayList<Airport> chosenAirportsList;
    private ArrayList<Airport> foundAirports;
    private static FindAirportsAdapter foundAdapter;
    private static ChosenAirportsAdapter chosenAdapter;
    private static String findQueryURL = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/%1$s/%2$s/%3$s/?query=%4$s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_search);

        if(getIntent().getBooleanExtra(getResources().getString(R.string.from_departure), true)) {
            chosenAirportsList = FlightsActivity.departureAirports;
        } else {
            chosenAirportsList = FlightsActivity.arrivalAirports;
        }

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearChosen = findViewById(R.id.btnClearChosen);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foundAirports.clear();
                foundAdapter.notifyDataSetChanged();
                String findAirport = etSearch.getText().toString();
                etSearch.setText("");
                if (findAirport.length() < 2) {
                    Toast.makeText(AirportSearchActivity.this, "Try searching with more letters", Toast.LENGTH_SHORT).show();
                    return;
                }
                findMatchingAirports(findAirport);
            }
        });
        btnClearChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Airport airport : chosenAirportsList) {
                    airport.flipChosen();
                }
                chosenAirportsList.clear();
                refreshChosenAirports();
                refreshFoundAirports();
            }
        });

        foundAirports = new ArrayList<>();
        foundAdapter = new FindAirportsAdapter(this, foundAirports, chosenAirportsList);
        chosenAdapter = new ChosenAirportsAdapter(this, chosenAirportsList);

        rvChosenAirports = findViewById(R.id.rvChosenAirports);
        rvFindAirport = findViewById(R.id.rvFindAirport);

        rvFindAirport.setLayoutManager(new LinearLayoutManager(this));
        rvFindAirport.setAdapter(foundAdapter);

        rvChosenAirports.setLayoutManager(new LinearLayoutManager(this));
        rvChosenAirports.setAdapter(chosenAdapter);
    }

    private void findMatchingAirports(String queryName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        String URL = String.format(findQueryURL, "US", "USD", "en", queryName);
        client.get(URL, headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        processAirports(json.jsonObject);
                        foundAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "failed");
                    }
                });
    }

    private void processAirports(JSONObject jsonObject) {
        try {
            JSONArray matchingPlaces = jsonObject.getJSONArray("Places");
            Boolean added;
            for (int i = 0; i < matchingPlaces.length(); i++) {
                added = false;
                JSONObject place = matchingPlaces.getJSONObject(i);
                for (int j = 0; j < chosenAirportsList.size(); j++) {
                    if (chosenAirportsList.get(j).getIATACode().equals(place.getString("PlaceId"))) {
                        foundAirports.add(chosenAirportsList.get(j));
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    Airport airport = new Airport(place.getString("PlaceName") + " Airport",
                            place.getString("PlaceId"), place.getString("CountryName"));
                    foundAirports.add(airport);
                }
            }
            if (matchingPlaces.length() == 0) {
                Toast.makeText(AirportSearchActivity.this, "No airports found, try broader search", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(AirportSearchActivity.this, "Unable to process airports", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void refreshChosenAirports() {
        chosenAdapter.notifyDataSetChanged();
    }
    public static void refreshFoundAirports() {
        foundAdapter.notifyDataSetChanged();
    }

}