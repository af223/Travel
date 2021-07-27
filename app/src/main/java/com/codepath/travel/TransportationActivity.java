package com.codepath.travel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Collections;

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
    private static int offset;
    private static String categoryParameter;
    private ProgressBar progressBarTransportation;
    private final ArrayList<Integer> typeList = new ArrayList<>();
    private final String[] typeAlias = {CATEGORIES, "carrental", "bikerentals", "taxis", "motorcycle_rental",
            "trainstations", "buses"};
    private final String[] typeArray = {"See all", "Car Rentals", "Bike Rentals", "Taxis", "Motorcycle Rental",
            "Train Stations", "Buses"};
    private Toolbar toolbar;
    private TextView tvTransportType;
    private boolean[] selectedType;
    private RecyclerView rvTransportations;
    private TransportationsAdapter adapter;
    private ArrayList<YelpData> transportations;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        offset = 0;
        progressBarTransportation = findViewById(R.id.progressBarTransportation);
        progressBarTransportation.setVisibility(View.VISIBLE);
        tvTransportType = findViewById(R.id.tvTransportType);
        selectedType = new boolean[typeArray.length];

        tvTransportType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategorySelecter();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Transportation");

        rvTransportations = findViewById(R.id.rvTransportations);
        transportations = new ArrayList<>();
        adapter = new TransportationsAdapter(this, transportations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTransportations.setLayoutManager(linearLayoutManager);
        rvTransportations.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTransportation();
            }
        };
        rvTransportations.addOnScrollListener(scrollListener);
        categoryParameter = CATEGORIES;
        loadTransportation();
    }

    private void loadTransportation() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("latitude", getIntent().getStringExtra(Destination.KEY_LAT));
        params.put("longitude", getIntent().getStringExtra(Destination.KEY_LONG));
        params.put("categories", categoryParameter);
        params.put("limit", NUM_LOAD_BUSINESSES);
        params.put("offset", offset);
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(YELP_BUSINESS_SEARCH_URL, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                YelpData.processYelpResults(transportations, json.jsonObject, TransportationActivity.this);
                adapter.notifyDataSetChanged();
                progressBarTransportation.setVisibility(View.GONE);
                offset += NUM_LOAD_BUSINESSES;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Tourist activities request failed: ", throwable);
                Toast.makeText(TransportationActivity.this, "Unable to find tourist activities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategorySelecter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransportationActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Select a category");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(typeArray, selectedType, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    typeList.add(which);
                    Collections.sort(typeList);
                } else {
                    typeList.remove(Integer.valueOf(which));
                }
            }
        });

        builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setUpToLoadResults();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < typeList.size(); i++) {
                    stringBuilder.append(typeArray[typeList.get(i)]);
                    categoryParameter += typeAlias[typeList.get(i)];

                    if (i != typeList.size() - 1) {
                        stringBuilder.append(", ");
                        categoryParameter += ",";
                    }
                }
                loadTransportation();
                tvTransportType.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetCategoryFilter();
                builder.show();
            }
        });

        builder.show();
    }

    private void resetCategoryFilter() {
        for (int i = 0; i < selectedType.length; i++) {
            selectedType[i] = false;
            typeList.clear();
            tvTransportType.setText("");
        }
    }

    private void setUpToLoadResults() {
        progressBarTransportation.setVisibility(View.VISIBLE);
        offset = 0;
        transportations.clear();
        scrollListener.resetState();
        categoryParameter = "";
    }
}