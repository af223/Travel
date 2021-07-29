package com.codepath.travel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.travel.adapters.TransportationsAdapter;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.FilterDialog;
import com.codepath.travel.models.YelpData;

import java.util.ArrayList;

import static com.codepath.travel.MainActivity.logout;

/**
 * This activity allows the user to find modes of transportation at the chosen destination.
 * <p>
 * This activity appears when the user chooses "transportation" from the expanded item in LocationsFragment.java. The intent passed in
 * contains the objectId for the selected destination.
 */

public class TransportationActivity extends AppCompatActivity {

    private static final String CATEGORIES = "transport,carrental,bikerentals,motorcycle_rental,trainstations";
    private static final String[] typeAlias = {CATEGORIES, "carrental", "bikerentals", "taxis", "motorcycle_rental",
                                                "trainstations", "buses"};
    private static final String[] typeArray = {"See all", "Car Rentals", "Bike Rentals", "Taxis", "Motorcycle Rental",
                                                "Train Stations", "Buses"};
    private static int offset;
    private static String categoryParameter;
    private final ArrayList<Integer> typeList = new ArrayList<>();
    private ProgressBar progressBarTransportation;
    private Toolbar toolbar;
    private TextView tvTransportType;
    private boolean[] selectedType;
    private RecyclerView rvTransportations;
    private TransportationsAdapter adapter;
    private ArrayList<YelpData> transportations;
    private EndlessRecyclerViewScrollListener scrollListener;
    private FilterDialog filterDialog;
    private String latitude;
    private String longitude;
    private Runnable transportRunnable;

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
                loadSearchResults();
            }
        };
        rvTransportations.addOnScrollListener(scrollListener);
        categoryParameter = CATEGORIES;

        filterDialog = new FilterDialog(selectedType, typeList, typeArray, tvTransportType);
        latitude = getIntent().getStringExtra(Destination.KEY_LAT);
        longitude = getIntent().getStringExtra(Destination.KEY_LONG);

        transportRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                progressBarTransportation.setVisibility(View.GONE);
                offset += YelpData.getNumLoadBusiness();
            }
        };

        loadSearchResults();
    }

    private void showCategorySelecter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransportationActivity.this, R.style.AppCompatAlertDialogStyle);
        filterDialog.buildSelectorDialog(builder);
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
                loadSearchResults();
                tvTransportType.setText(stringBuilder.toString());
            }
        });

        builder.show();
    }

    private void setUpToLoadResults() {
        progressBarTransportation.setVisibility(View.VISIBLE);
        offset = 0;
        transportations.clear();
        scrollListener.resetState();
        categoryParameter = "";
    }

    private void loadSearchResults() {
        RequestParams params = YelpData.createRequestParams(latitude, longitude, categoryParameter, "", offset);
        YelpData.loadDataFromYelp(params, transportations, TransportationActivity.this, transportRunnable);
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