package com.codepath.travel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.models.Destination;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Headers;

public class TouristSpotsActivity extends AppCompatActivity {

    private static final String TAG = "TouristSpotsActivity";
    private static final String YELP_BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private TextView tvActivityType;
    private boolean[] selectedType;
    private ArrayList<Integer> typeList = new ArrayList<>();
    private String[] typeArray = {"Amusement Parks", "Art Galleries", "Beaches", "Gardens", "Hiking",
                                "Landmarks/Historical Buildings", "Museums", "Nightlife", "Shopping",
                                "Spas", "Sports", "Tours"};
    private String[] typeAlias = {"amusementparks", "galleries", "beaches", "gardens", "hiking",
                                "landmarks", "museums", "nightlife", "shopping",
                                "spas", "active", "tours"};
    private Destination currDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spots);

        tvActivityType = findViewById(R.id.tvActivityType);

        selectedType = new boolean[typeArray.length];

        getChosenDestination();
    }

    private void getChosenDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.getInBackground(getIntent().getStringExtra(Destination.KEY_OBJECT_ID), new GetCallback<Destination>() {
            @Override
            public void done(Destination destination, ParseException e) {
                currDestination = destination;

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
                                    typeList.remove(which);
                                }
                            }
                        });

                        builder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String categoryParameter = "";
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < typeList.size(); i++) {
                                    stringBuilder.append(typeArray[typeList.get(i)]);
                                    categoryParameter += typeAlias[typeList.get(i)];

                                    if (i != typeList.size()-1) {
                                        stringBuilder.append(", ");
                                        categoryParameter += ",";
                                    }
                                }
                                loadTouristResults(categoryParameter);
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
                            }
                        });

                        builder.show();
                    }
                });
            }
        });
    }

    private void loadTouristResults(String categoryParameter) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        params.put("latitude", currDestination.getLatitude());
        params.put("longitude", currDestination.getLongitude());
        params.put("categories", categoryParameter);
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(YELP_BUSINESS_SEARCH_URL, headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Tourist activities request failed: ", throwable);
                        Toast.makeText(TouristSpotsActivity.this, "Unable to find tourist activities", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}