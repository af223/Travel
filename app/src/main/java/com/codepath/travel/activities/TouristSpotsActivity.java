package com.codepath.travel.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.travel.EndlessRecyclerViewScrollListener;
import com.codepath.travel.R;
import com.codepath.travel.adapters.TouristActivitiesAdapter;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.FilterDialog;
import com.codepath.travel.models.YelpData;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

import static com.codepath.travel.activities.MainActivity.logout;

/**
 * This activity allows the user to find suggested tourist spots/activities near the chosen destination.
 * The user can filter by selecting one or more of the suggested categories, or search by keyword.
 * <p>
 * This activity appears when the user chooses "find activity" from the expanded item in LocationsFragment.java. The intent passed in
 * contains the objectId for the selected destination.
 */

public class TouristSpotsActivity extends AppCompatActivity {

    public static final int CHOOSE_TOURIST_SPOTS_CODE = 7;
    public static final int CHOOSE_RESTAURANTS_CODE = 12;
    private static final String[] TYPE_ALIAS = {"amusementparks", "galleries", "beaches", "gardens", "hiking",
            "landmarks", "museums", "nightlife", "shopping",
            "spas", "active", "tours"};
    private static final String[] TYPE_ARRAY = {"Amusement Parks", "Art Galleries", "Beaches", "Gardens", "Hiking",
            "Landmarks/Historical Buildings", "Museums", "Nightlife", "Shopping",
            "Spas", "Sports", "Tours"};
    private static String categoryParameter;
    private static String destinationID;
    private static Destination currDestination;
    private static int offset;
    private static String keywordQuery = "";
    private final ArrayList<Integer> typeList = new ArrayList<>();
    private TextView tvActivityType;
    private ProgressBar pbTouristLoad;
    private Toolbar toolbar;
    private boolean[] selectedType;
    private RecyclerView rvTouristActivities;
    private TouristActivitiesAdapter adapter;
    private ArrayList<YelpData> touristSpots;
    private String latitude;
    private String longitude;
    private EndlessRecyclerViewScrollListener scrollListener;
    private FilterDialog filterDialog;
    private final Runnable touristSpotRunnable = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            pbTouristLoad.setVisibility(View.GONE);
            offset += YelpData.getNumLoadBusiness();
        }
    };
    private Boolean isRestaurant;
    private SearchView svTouristSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spots);
        isRestaurant = getIntent().getIntExtra(getResources().getString(R.string.activity_type), CHOOSE_TOURIST_SPOTS_CODE) == CHOOSE_RESTAURANTS_CODE;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvActivityType = findViewById(R.id.tvActivityType);
        svTouristSpot = findViewById(R.id.svTouristSpot);
        if (isRestaurant) {
            getSupportActionBar().setTitle("Find Restaurants");
            tvActivityType.setVisibility(View.GONE);
            categoryParameter = "food,restaurants";
        } else {
            getSupportActionBar().setTitle("Find Activities");
            selectedType = new boolean[TYPE_ARRAY.length];
            filterDialog = new FilterDialog(selectedType, typeList, TYPE_ARRAY, tvActivityType);
        }

        rvTouristActivities = findViewById(R.id.rvTouristActivities);
        pbTouristLoad = findViewById(R.id.pbTouristLoad);

        touristSpots = new ArrayList<>();
        latitude = getIntent().getStringExtra(Destination.KEY_LAT);
        longitude = getIntent().getStringExtra(Destination.KEY_LONG);
        destinationID = getIntent().getStringExtra(Destination.KEY_OBJECT_ID);
        offset = 0;

        loadDestination();
    }

    private void loadDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.getInBackground(destinationID, new GetCallback<Destination>() {
            @Override
            public void done(Destination object, ParseException e) {
                currDestination = object;
                setupRecyclerView();
                if (isRestaurant) {
                    loadSearchResults();
                }
                svTouristSpot.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query.isEmpty()) {
                            Toast.makeText(TouristSpotsActivity.this, "No keywords entered", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        setUpToLoadResults();
                        if (!isRestaurant) {
                            filterDialog.resetCategoryFilter();
                            tvActivityType.setText(keywordQuery);
                        }
                        keywordQuery = query;
                        loadSearchResults();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                if (!isRestaurant) {
                    tvActivityType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCategorySelecter();
                        }
                    });
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TouristActivitiesAdapter(TouristSpotsActivity.this, touristSpots, currDestination, isRestaurant, null);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvTouristActivities.setLayoutManager(gridLayoutManager);
        rvTouristActivities.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadSearchResults();
            }
        };
        rvTouristActivities.addOnScrollListener(scrollListener);
    }

    private void showCategorySelecter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TouristSpotsActivity.this, R.style.AppCompatAlertDialogStyle);
        filterDialog.buildSelectorDialog(builder);
        builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setUpToLoadResults();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < typeList.size(); i++) {
                    stringBuilder.append(TYPE_ARRAY[typeList.get(i)]);
                    categoryParameter += TYPE_ALIAS[typeList.get(i)];

                    if (i != typeList.size() - 1) {
                        stringBuilder.append(", ");
                        categoryParameter += ",";
                    }
                }
                loadSearchResults();
                tvActivityType.setText(stringBuilder.toString());
            }
        });

        builder.show();
    }

    private void setUpToLoadResults() {
        pbTouristLoad.setVisibility(View.VISIBLE);
        offset = 0;
        touristSpots.clear();
        scrollListener.resetState();
        keywordQuery = "";
        if (!isRestaurant)
            categoryParameter = "";
    }

    private void loadSearchResults() {
        RequestParams params = YelpData.createRequestParams(latitude, longitude, categoryParameter, keywordQuery, offset);
        YelpData.loadDataFromYelp(params, touristSpots, TouristSpotsActivity.this, touristSpotRunnable);
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