package com.codepath.travel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.TransportationsAdapter;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.YelpData;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * This activity allows the user to find modes of transportation at the chosen destination.
 * <p>
 * This activity appears when the user chooses "transportation" from the expanded item in LocationsFragment.java. The intent passed in
 * contains the objectId for the selected destination.
 */

public class TransportationActivity extends AppCompatActivity {

    private static final String TAG = "TransportationActivity";
    private static final String YELP_BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String CATEGORIES = "transport,carrental,bikerentals,motorcycle_rental,trainstations";
    private static final Integer NUM_LOAD_BUSINESSES = 25;
    private RecyclerView rvTransportations;
    private TransportationsAdapter adapter;
    private ArrayList<YelpData> transportations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        rvTransportations = findViewById(R.id.rvTransportations);
        transportations = new ArrayList<>();
        adapter = new TransportationsAdapter(this, transportations);
        rvTransportations.setLayoutManager(new LinearLayoutManager(this));
        rvTransportations.setAdapter(adapter);

        loadTransportation();
    }

    private void loadTransportation() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("latitude", getIntent().getStringExtra(Destination.KEY_LAT));
        params.put("longitude", getIntent().getStringExtra(Destination.KEY_LONG));
        params.put("categories", CATEGORIES);
        params.put("limit", NUM_LOAD_BUSINESSES);
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(YELP_BUSINESS_SEARCH_URL, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                YelpData.processYelpResults(transportations, json.jsonObject, TransportationActivity.this);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Tourist activities request failed: ", throwable);
                Toast.makeText(TransportationActivity.this, "Unable to find tourist activities", Toast.LENGTH_SHORT).show();
            }
        });
    }

}