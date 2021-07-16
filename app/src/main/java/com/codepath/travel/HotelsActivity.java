package com.codepath.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Hotel;
import com.codepath.travel.models.HotelOffer;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.ByteString;

public class HotelsActivity extends AppCompatActivity {

    private static final String AMADEUS_ACCESS_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String AMADEUS_HOTEL_URL = "https://test.api.amadeus.com/v2/shopping/hotel-offers";
    private static final String TAG = "HotelsActivity";
    private ArrayList<Hotel> hotels;
    private Destination currDestination;
    private GoogleMap map;
    private Hotel chosenHotel;
    private TextView tvHotelName;
    private RatingBar rbRating;
    private TextView tvPhone;
    private TextView tvEmail;
    private Button btnSelect;
    private TextView tvAddress;
    private TextView tvCheckInDate;
    private TextView tvCheckOutDate;
    private TextView tvNumBeds;
    private TextView tvCost;
    private TextView tvDescription;
    private ArrayList<MarkerOptions> markers;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            getChosenDestination();
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    for (int i = 0; i < markers.size(); i++) {
                        if (marker.getPosition().equals(markers.get(i).getPosition())) {
                            chosenHotel = hotels.get(i);
                            break;
                        }
                    }
                    displayChosenHotel();
                    return true;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        //requestAccessToken(); last : 8:13 PM

        tvHotelName = findViewById(R.id.tvHotelName);
        rbRating = findViewById(R.id.rbRating);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvNumBeds = findViewById(R.id.tvNumBeds);
        tvCost = findViewById(R.id.tvCost);
        tvDescription = findViewById(R.id.tvDescription);
        btnSelect = findViewById(R.id.btnSelect);
        hotels = new ArrayList<>();
        markers = new ArrayList<>();
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chosenHotel == null) {
                    Toast.makeText(HotelsActivity.this, "Must select a hotel", Toast.LENGTH_SHORT).show();
                } else {
                    saveHotel(chosenHotel);
                }
            }
        });
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.i(TAG, "getting map");
            mapFragment.getMapAsync(callback);
        }
    }

    private void requestAccessToken() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        RequestHeaders headers = new RequestHeaders();
        headers.put("content-type", "application/x-www-form-urlencoded");
        String body = String.format("grant_type=client_credentials&client_id=%1$s&client_secret=%2$s",
                                        getResources().getString(R.string.amadeus_api_key),
                                        getResources().getString(R.string.amadeus_api_secret));
        Request.Builder requestBuilder = new Request.Builder().url(AMADEUS_ACCESS_URL);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/x-www-form-urlencoded"));
        Request request = requestBuilder.post(requestBody).build();
        JsonHttpResponseHandler callback = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "ERROR: ", throwable);
                Log.e(TAG, response);
            }
        };
        okHttpClient.newCall(request).enqueue(callback);
    }

    private void getChosenDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        Log.i(TAG, getIntent().getStringExtra(Destination.KEY_OBJECT_ID));
        query.getInBackground(getIntent().getStringExtra(Destination.KEY_OBJECT_ID), new GetCallback<Destination>() {
            @Override
            public void done(Destination destination, ParseException e) {
                currDestination = destination;
                findHotels(destination);
            }
        });
    }

    private void findHotels(Destination destination) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        String authorization = "Bearer " + getResources().getString(R.string.amadeus_token);
        headers.put("authorization", authorization);
        params.put("latitude", destination.getLatitude());
        params.put("longitude", destination.getLongitude());
        params.put("radius", "50");
        params.put("radiusUnit", "MILE");
        params.put("currency", "USD");
        client.get(AMADEUS_HOTEL_URL, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                try {
                    processHotels(json.jsonObject.getJSONArray("data"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(destination.getCoords()));
                    map.moveCamera(CameraUpdateFactory.zoomBy(9));
                    Log.i(TAG, "done processing");
                } catch (JSONException e) {
                    Log.i(TAG, "Unable to parse rsponse");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "ERROR: ", throwable);
                Log.e(TAG, response);
            }
        });
    }

    private void processHotels(JSONArray hotelsList) {
        try {
            for (int i = 0; i < hotelsList.length(); i++) {
                JSONObject hotelOfferObject = hotelsList.getJSONObject(i);
                JSONObject hotelJson = hotelOfferObject.getJSONObject("hotel");
                ArrayList<HotelOffer> offers = processHotelOffers(hotelOfferObject.getJSONArray("offers"));
                String name = hotelJson.getString("name");
                Integer rating = 0;
                if (hotelJson.has("rating")) {
                    rating = Integer.parseInt(hotelJson.getString("rating"));
                }
                String latitude = String.valueOf(hotelJson.getDouble("latitude"));
                String longitude = String.valueOf(hotelJson.getDouble("longitude"));
                String address = "N/A";
                if (hotelJson.has("address")) {
                    address = "";
                    if (hotelJson.getJSONObject("address").has("lines")) {
                        for (int j = 0; j < hotelJson.getJSONObject("address").getJSONArray("lines").length(); j++) {
                            address += hotelJson.getJSONObject("address").getJSONArray("lines").get(j) + ", ";
                        }
                    }
                    if (hotelJson.getJSONObject("address").has("cityName")) {
                        address += hotelJson.getJSONObject("address").getString("cityName") + ", ";
                    }
                    if (hotelJson.getJSONObject("address").has("postalCode")) {
                        address += hotelJson.getJSONObject("address").getString("postalCode") + ", ";
                    }
                    if (hotelJson.getJSONObject("address").has("countryCode")) {
                        address += hotelJson.getJSONObject("address").getString("countryCode") + ", ";
                    }
                }
                String phoneNumber = "N/A";
                if (hotelJson.getJSONObject("contact").has("phone")) {
                    phoneNumber = hotelJson.getJSONObject("contact").getString("phone");
                }
                String email = "N/A";
                if (hotelJson.getJSONObject("contact").has("email")) {
                    email = hotelJson.getJSONObject("contact").getString("email");
                }
                String description = "N/A";
                if (hotelJson.has("description")) {
                    description = hotelJson.getJSONObject("description").getString("text");
                }
                Hotel newHotel = new Hotel(name, address, latitude, longitude, phoneNumber, rating, email, description, offers);
                hotels.add(newHotel);
                markHotelOnMap(newHotel);
            }
        }  catch (JSONException e) {
            Log.i(TAG, "Unable to parse hotels");
            e.printStackTrace();
        }
    }

    private ArrayList<HotelOffer> processHotelOffers(JSONArray hotelOffersList) {
        ArrayList<HotelOffer> offersList = new ArrayList<>();
        try {
            for (int i = 0; i < hotelOffersList.length(); i++) {
                JSONObject offerJson = hotelOffersList.getJSONObject(i);
                String checkInDate = offerJson.getString("checkInDate");
                String checkOutDate = offerJson.getString("checkOutDate");
                String roomType = offerJson.getJSONObject("room").getJSONObject("typeEstimated").getString("category");
                Integer numBeds = offerJson.getJSONObject("room").getJSONObject("typeEstimated").getInt("beds");
                String description = "N/A";
                if (offerJson.getJSONObject("room").has("description")) {
                    description = offerJson.getJSONObject("room").getJSONObject("description").getString("text");
                }
                Integer numAdults = offerJson.getJSONObject("guests").getInt("adults");
                String cost = offerJson.getJSONObject("price").getString("total");
                offersList.add(new HotelOffer(checkInDate, checkOutDate, roomType, numBeds, description, numAdults, cost));
            }
        } catch (JSONException e) {
            Log.i(TAG, "Unable to parse offers");
            e.printStackTrace();
        }
        return offersList;
    }

    private void markHotelOnMap(Hotel hotel) {
        MarkerOptions marker = new MarkerOptions().position(hotel.getCoords()).title(hotel.getName());
        map.addMarker(marker);
        markers.add(marker);
        Log.i(TAG, "added Markers");
    }

    private void saveHotel(Hotel hotel) {
        currDestination.setHotelName(hotel.getName());
        currDestination.setHotelLat(hotel.getLatitude());
        currDestination.setHotelLong(hotel.getLongitude());
        currDestination.setHotelCost(hotel.getOffers().get(0).getCost());
        currDestination.setHotelAddress(hotel.getAddress());
        currDestination.setHotelPhone(hotel.getPhone());
        currDestination.setHotelDescription(hotel.getDescription());
        currDestination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(HotelsActivity.this, "Unable to save hotel", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(HotelsActivity.this, "Hotel saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChosenHotel() {
        tvHotelName.setText(chosenHotel.getName());
        rbRating.setRating(chosenHotel.getRating());
        tvPhone.setText(chosenHotel.getPhone());
        tvEmail.setText(chosenHotel.getEmail());
        tvAddress.setText(chosenHotel.getAddress());
        if (chosenHotel.getOffers().isEmpty()) {
            tvDescription.setText("Information available");
            tvCheckInDate.setText("N/A");
            tvCheckOutDate.setText("N/A");
            tvNumBeds.setText("N/A");
            tvCost.setText("N/A");
        } else {
            HotelOffer chosenOffer = chosenHotel.getOffers().get(0);
            if (chosenOffer.getDescription().equals("N/A")) {
                if (chosenHotel.getDescription().equals("N/A")) {
                    tvDescription.setVisibility(View.GONE);
                } else {
                    tvDescription.setVisibility(View.VISIBLE);
                    tvDescription.setText(chosenHotel.getDescription());
                }
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(chosenOffer.getDescription());
            }
            tvCheckInDate.setText(chosenOffer.getCheckInDate());
            tvCheckOutDate.setText(chosenOffer.getCheckOutDate());
            tvNumBeds.setText(String.valueOf(chosenOffer.getNumBeds()));
            String money = "$" + chosenOffer.getCost();
            tvCost.setText(money);
        }
    }
}