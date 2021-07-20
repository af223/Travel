package com.codepath.travel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.adapters.TouristActivitiesAdapter;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.TouristDestination;
import com.codepath.travel.models.TouristSpot;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Headers;

/**
 * This activity allows the user to find suggested tourist spots/activities near the chosen destination.
 * The user can filter by selecting one or more of the suggested categories.
 *
 * This activity appears when the user chooses "find activity" from ResourcesFragment.java. The intent passed in
 * contains the objectId for the selected destination.
 */

public class TouristSpotsActivity extends AppCompatActivity {

    private static final String TAG = "TouristSpotsActivity";
    private static final String YELP_BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final int NUM_LOAD_BUSINESSES = 20;
    private TextView tvActivityType;
    private ProgressBar pbTouristLoad;
    private boolean[] selectedType;
    private ArrayList<Integer> typeList = new ArrayList<>();
    private String[] typeArray = {"Amusement Parks", "Art Galleries", "Beaches", "Gardens", "Hiking",
                                "Landmarks/Historical Buildings", "Museums", "Nightlife", "Shopping",
                                "Spas", "Sports", "Tours"};
    private String[] typeAlias = {"amusementparks", "galleries", "beaches", "gardens", "hiking",
                                "landmarks", "museums", "nightlife", "shopping",
                                "spas", "active", "tours"};
    private RecyclerView rvTouristActivities;
    private TouristActivitiesAdapter adapter;
    private ArrayList<TouristSpot> touristSpots;
    private String latitude;
    private String longitude;
    private static String destinationID;
    private static Destination currDestination;
    private static int offset;
    private String categoryParameter;
    private EndlessRecyclerViewScrollListener scrollListener;

    public static void saveTouristDestination(TouristSpot touristSpot) {
        TouristDestination touristDestination = new TouristDestination();
        touristDestination.setUser(ParseUser.getCurrentUser());
        touristDestination.setPlaceId(touristSpot.getPlaceId());
        if (currDestination != null) {
            touristDestination.setDestination(currDestination);
            touristDestination.saveInBackground();
        } else {
            ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
            query.getInBackground(destinationID, new GetCallback<Destination>() {
                @Override
                public void done(Destination object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Unable to save tourist destination");
                    }
                    currDestination = object;
                    touristDestination.setDestination(currDestination);
                    touristDestination.saveInBackground();
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spots);

        tvActivityType = findViewById(R.id.tvActivityType);
        rvTouristActivities = findViewById(R.id.rvTouristActivities);
        pbTouristLoad = findViewById(R.id.pbTouristLoad);

        touristSpots = new ArrayList<>();
        adapter = new TouristActivitiesAdapter(this, touristSpots);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTouristActivities.setLayoutManager(gridLayoutManager);
        rvTouristActivities.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTouristResults();
            }
        };
        rvTouristActivities.addOnScrollListener(scrollListener);

        selectedType = new boolean[typeArray.length];
        latitude = getIntent().getStringExtra(Destination.KEY_LAT);
        longitude = getIntent().getStringExtra(Destination.KEY_LONG);
        destinationID = getIntent().getStringExtra(Destination.KEY_OBJECT_ID);
        offset = 0;

        tvActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TouristSpotsActivity.this);
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
                        pbTouristLoad.setVisibility(View.VISIBLE);
                        offset = 0;
                        touristSpots.clear();
                        scrollListener.resetState();
                        categoryParameter = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < typeList.size(); i++) {
                            stringBuilder.append(typeArray[typeList.get(i)]);
                            categoryParameter += typeAlias[typeList.get(i)];

                            if (i != typeList.size()-1) {
                                stringBuilder.append(", ");
                                categoryParameter += ",";
                            }
                        }
                        loadTouristResults();
                        tvActivityType.setText(stringBuilder.toString());
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
                        for (int i = 0; i < selectedType.length; i++) {
                            selectedType[i] = false;
                            typeList.clear();
                            tvActivityType.setText("");
                        }
                        builder.show();
                    }
                });

                builder.show();
            }
        });
    }

    private void loadTouristResults() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("categories", categoryParameter);
        params.put("limit", NUM_LOAD_BUSINESSES);
        params.put("offset", offset);
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(YELP_BUSINESS_SEARCH_URL, headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        processYelpResults(json.jsonObject);
                        adapter.notifyDataSetChanged();
                        pbTouristLoad.setVisibility(View.GONE);
                        offset += NUM_LOAD_BUSINESSES;
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Tourist activities request failed: ", throwable);
                        Toast.makeText(TouristSpotsActivity.this, "Unable to find tourist activities", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processYelpResults(JSONObject jsonObject) {
        try {
            JSONArray businesses = jsonObject.getJSONArray("businesses");
            for (int i = 0; i < businesses.length(); i++) {
                JSONObject business = businesses.getJSONObject(i);
                String name = business.getString("name");
                String rating = String.valueOf(business.getDouble("rating"));
                String imageURL = business.getString("image_url");
                String yelpURL = business.getString("url");
                String placeId = business.getString("id");
                touristSpots.add(new TouristSpot(name, rating, imageURL, yelpURL, placeId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(TouristSpotsActivity.this, "Unable to process Yelp results", Toast.LENGTH_SHORT).show();
        }
    }
}