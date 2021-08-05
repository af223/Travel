package com.codepath.travel.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.adapters.ChosenAirportsAdapter;
import com.codepath.travel.adapters.FindAirportsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Destination;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * This activity allows the user to search for and select the possible arrival and departure airports
 * that they want to see plane tickets for. For arrival airports (at the selected travel location), suggested
 * airports are automatically loaded. Users can see choose airports marked (General) to see plane tickets from
 * any airport in that area.
 * <p>
 * This activity appears when the user clicks the "select [arrival/departure] airport" buttons from FlightsActivity.java.
 */

public class AirportSearchActivity extends AppCompatActivity {

    private static final String TAG = "AirportSearchActivity";
    private static final String FIND_QUERY_URL = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/%1$s/%2$s/%3$s/?query=%4$s";
    private static FindAirportsAdapter foundAdapter;
    private static ChosenAirportsAdapter chosenAdapter;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvChosenAirports;
    private RecyclerView rvFindAirport;
    private Button btnClearChosen;
    private ProgressBar pbAirport;
    private ArrayList<Airport> chosenAirportsList;
    private ArrayList<Airport> foundAirports;
    private Gson gson;
    private Boolean loadingAirportSuggestions = false;

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
            chosenAirportsList = FlightsActivity.DEPARTURE_AIRPORTS;
        } else {
            chosenAirportsList = FlightsActivity.ARRIVAL_AIRPORTS;
        }

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        pbAirport = findViewById(R.id.pbAirport);
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
                pbAirport.setVisibility(View.VISIBLE);
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
            pbAirport.setVisibility(View.VISIBLE);
            loadSuggestedAirports();
            loadingAirportSuggestions = true;
        }
    }

    private void findMatchingAirports(String queryName) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        String URL = String.format(FIND_QUERY_URL, "US", "USD", "en", queryName);
        client.get(URL, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                MatchedAirports airports = gson.fromJson(String.valueOf(json.jsonObject), MatchedAirports.class);
                displayAirports(airports);
                foundAdapter.notifyDataSetChanged();
                pbAirport.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "failed to get airports: ", throwable);
                Toast.makeText(AirportSearchActivity.this, "Unable to find airports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAirports(MatchedAirports airports) {
        ArrayList<AirportInfo> airportInfos = airports.getAirports();
        Boolean added;
        for (AirportInfo place : airportInfos) {
            added = false;
            for (int j = 0; j < chosenAirportsList.size(); j++) {
                if (chosenAirportsList.get(j).getIATACode().equals(place.getPlaceId())) {
                    foundAirports.add(chosenAirportsList.get(j));
                    added = true;
                    break;
                }
            }
            if (!added
                    && (!loadingAirportSuggestions
                    || place.getCountryName().equals(getIntent().getStringExtra(Destination.KEY_COUNTRY)))) {
                Airport airport;
                if (place.getCountryId().equals(place.getPlaceId())
                        || place.getCityId().equals(place.getPlaceId())) {
                    airport = new Airport(place.getPlaceName() + " Airport (General)",
                            place.getPlaceId(), place.getCountryName());
                } else {
                    airport = new Airport(place.getPlaceName() + " Airport",
                            place.getPlaceId(), place.getCountryName());
                }
                foundAirports.add(airport);
            }
        }
        if (foundAirports.isEmpty()) {
            if (loadingAirportSuggestions) {
                loadingAirportSuggestions = false;
                findMatchingAirports(getIntent().getStringExtra(Destination.KEY_COUNTRY));
            } else {
                Toast.makeText(AirportSearchActivity.this, "No airports found, try broader search", Toast.LENGTH_LONG).show();
            }
        }
        loadingAirportSuggestions = false;
    }

    private void loadSuggestedAirports() {
        if (getIntent().getStringExtra(Destination.KEY_ADMIN1) != null) {
            findMatchingAirports(getIntent().getStringExtra(Destination.KEY_ADMIN1));
        } else {
            loadingAirportSuggestions = false;
            findMatchingAirports(getIntent().getStringExtra(Destination.KEY_COUNTRY));
        }
    }

    class MatchedAirports {
        private final ArrayList<AirportInfo> Places = new ArrayList<AirportInfo>();

        public ArrayList<AirportInfo> getAirports() {
            return Places;
        }
    }

    class AirportInfo {
        private String PlaceId;
        private String PlaceName;
        private String CountryId;
        private String CityId;
        private String CountryName;

        public String getPlaceId() {
            return PlaceId;
        }

        public String getPlaceName() {
            return PlaceName;
        }

        public String getCountryId() {
            return CountryId;
        }

        public String getCityId() {
            return CityId;
        }

        public String getCountryName() {
            return CountryName;
        }
    }
}