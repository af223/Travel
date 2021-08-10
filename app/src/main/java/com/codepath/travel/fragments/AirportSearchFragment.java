package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.activities.ChooseAirportsActivity;
import com.codepath.travel.adapters.FindAirportsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Destination;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * This fragment is displayed by AirportPagerAdapter in the ChooseAirportsActivity.
 * It allows user to see and edit their list of chosen airports.
 */
public class AirportSearchFragment extends Fragment {

    private static final String TAG = "AirportSearchFragment";
    private static final String FIND_QUERY_URL = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/%1$s/%2$s/%3$s/?query=%4$s";
    private static FindAirportsAdapter foundAdapter;
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvFindAirport;
    private ProgressBar pbAirport;
    private ArrayList<Airport> foundAirports;
    private Gson gson;
    private Boolean loadingAirportSuggestions = false;
    private Boolean isFromDeparture;
    private String admin1;
    private String country;

    public AirportSearchFragment() {
        // Required empty public constructor
    }

    public AirportSearchFragment(Intent intent, Boolean isFromDeparture) {
        this.isFromDeparture = isFromDeparture;
        this.admin1 = intent.getStringExtra(Destination.KEY_ADMIN1);
        this.country = intent.getStringExtra(Destination.KEY_COUNTRY);
    }

    public static void refreshFoundAirports() {
        foundAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airport_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        pbAirport = view.findViewById(R.id.pbAirport);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foundAirports.clear();
                String findAirport = etSearch.getText().toString();
                etSearch.setText("");
                if (findAirport.length() < 2) {
                    Toast.makeText(getContext(), "Try searching with more letters", Toast.LENGTH_SHORT).show();
                    return;
                }
                pbAirport.setVisibility(View.VISIBLE);
                findMatchingAirports(findAirport);
            }
        });

        foundAirports = new ArrayList<>();
        foundAdapter = new FindAirportsAdapter(getContext(), foundAirports, ChooseAirportsActivity.chosenAirportsList);
        rvFindAirport = view.findViewById(R.id.rvFindAirport);
        rvFindAirport.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFindAirport.setAdapter(foundAdapter);

        if (!isFromDeparture) {
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
                Toast.makeText(getContext(), "Unable to find airports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAirports(MatchedAirports airports) {
        ArrayList<AirportInfo> airportInfos = airports.getAirports();
        Boolean added;
        for (AirportInfo place : airportInfos) {
            added = false;
            for (int j = 0; j < ChooseAirportsActivity.chosenAirportsList.size(); j++) {
                if (ChooseAirportsActivity.chosenAirportsList.get(j).getIATACode().equals(place.getPlaceId())) {
                    foundAirports.add(ChooseAirportsActivity.chosenAirportsList.get(j));
                    added = true;
                    break;
                }
            }
            if (!added
                    && (!loadingAirportSuggestions
                    || place.getCountryName().equals(country))) {
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
                findMatchingAirports(country);
            } else {
                Toast.makeText(getContext(), "No airports found, try broader search", Toast.LENGTH_LONG).show();
            }
        }
        loadingAirportSuggestions = false;
    }

    private void loadSuggestedAirports() {
        if (admin1 != null) {
            findMatchingAirports(admin1);
        } else {
            loadingAirportSuggestions = false;
            findMatchingAirports(country);
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