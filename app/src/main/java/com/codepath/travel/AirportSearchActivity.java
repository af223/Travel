package com.codepath.travel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.ChosenAirportsAdapter;
import com.codepath.travel.adapters.FindAirportsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Destination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

public class AirportSearchActivity extends AppCompatActivity {

    private static final String TAG = "AirportSearchActivity";
    private static FindAirportsAdapter foundAdapter;
    private static ChosenAirportsAdapter chosenAdapter;
    private static final String findQueryURL = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/%1$s/%2$s/%3$s/?query=%4$s";
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvChosenAirports;
    private RecyclerView rvFindAirport;
    private Button btnClearChosen;
    private Boolean loadingSuggested;
    private ArrayList<Airport> chosenAirportsList;
    private ArrayList<Airport> foundAirports;

    public static void refreshChosenAirports() {
        chosenAdapter.notifyDataSetChanged();
    }

    public static void refreshFoundAirports() {
        foundAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_search);

        if (getIntent().getBooleanExtra(getResources().getString(R.string.from_departure), true)) {
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
        rvFindAirport = findViewById(R.id.rvFindAirport);
        rvFindAirport.setLayoutManager(new LinearLayoutManager(this));
        rvFindAirport.setAdapter(foundAdapter);

        chosenAdapter = new ChosenAirportsAdapter(this, chosenAirportsList);
        rvChosenAirports = findViewById(R.id.rvChosenAirports);
        rvChosenAirports.setLayoutManager(new LinearLayoutManager(this));
        rvChosenAirports.setAdapter(chosenAdapter);

        if (!getIntent().getBooleanExtra(getResources().getString(R.string.from_departure), true)) {
            loadSuggestedAirports();
        }
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
                displayAirports(json.jsonObject);
                foundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "failed to get airports: ", throwable);
                Toast.makeText(AirportSearchActivity.this, "Unable to find airports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAirports(JSONObject jsonObject) {
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
                    if (!loadingSuggested || place.getString("CountryName").equals(getIntent().getStringExtra(Destination.KEY_COUNTRY))) {
                        Airport airport = new Airport(place.getString("PlaceName") + " Airport",
                                place.getString("PlaceId"), place.getString("CountryName"));
                        foundAirports.add(airport);
                    }
                }
            }
            if (matchingPlaces.length() == 0) {
                if (loadingSuggested) {
                    findMatchingAirports(getIntent().getStringExtra(Destination.KEY_COUNTRY));
                    loadingSuggested = false;
                } else {
                    Toast.makeText(AirportSearchActivity.this, "No airports found, try broader search", Toast.LENGTH_LONG).show();
                }

            }
        } catch (JSONException e) {
            Toast.makeText(AirportSearchActivity.this, "Unable to process airports", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error parsing airports JSON: ", e);
            e.printStackTrace();
        }
    }

    private void loadSuggestedAirports() {
        findMatchingAirports(getIntent().getStringExtra(Destination.KEY_ADMIN1));
        loadingSuggested = true;
    }
}