package com.codepath.travel.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Hotel;
import com.codepath.travel.models.HotelOffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.codepath.travel.activities.MainActivity.logout;
import static com.codepath.travel.activities.MainActivity.okHttpClient;

/**
 * This activity allows the user to see and select suggested hotels near the destination pinned on a map.
 * The user can see details about the hotel and its room offerings by clicking on its pin on the map.
 * <p>
 * This activity appears when the user chooses hotel from the expanded item in LocationsFragment.java. The intent passed in
 * contains the objectId for the selected destination.
 */

public class HotelsActivity extends AppCompatActivity {

    private static final String AMADEUS_ACCESS_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String AMADEUS_HOTEL_URL = "https://test.api.amadeus.com/v2/shopping/hotel-offers";
    private static final String TAG = "HotelsActivity";
    private final ArrayList<MarkerOptions> markers = new ArrayList<>();
    private final ArrayList<Hotel> hotels = new ArrayList<>();
    private String oauthToken;
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
    private Toolbar toolbar;
    private RelativeLayout rlProgressBar;
    private Gson gson;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            rlProgressBar.setVisibility(View.VISIBLE);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    for (int i = 0; i < markers.size(); i++) {
                        if (marker.getPosition().equals(markers.get(i).getPosition())) {
                            chosenHotel = hotels.get(i);
                            break;
                        }
                    }
                    if (chosenHotel == null) {
                        Toast.makeText(HotelsActivity.this, "Unable to display hotel", Toast.LENGTH_SHORT).show();
                    } else {
                        displayChosenHotel();
                    }
                    return true;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        requestAccessToken();

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hotels");

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
        rlProgressBar = findViewById(R.id.rlProgressBar);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void requestAccessToken() {
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
                try {
                    oauthToken = json.jsonObject.getString("access_token");
                    getChosenDestination();
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to get authentication");
                    Toast.makeText(HotelsActivity.this, "Unable to retrieve hotels", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "ERROR: ", throwable);
            }
        };
        okHttpClient.newCall(request).enqueue(callback);
    }

    private void getChosenDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.getInBackground(getIntent().getStringExtra(Destination.KEY_OBJECT_ID), new GetCallback<Destination>() {
            @Override
            public void done(Destination destination, ParseException e) {
                currDestination = destination;
                map.moveCamera(CameraUpdateFactory.newLatLng(destination.getCoords()));
                map.animateCamera(CameraUpdateFactory.zoomTo(7));
                findHotels(destination);
            }
        });
    }

    private void findHotels(Destination destination) {
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        String authorization = "Bearer " + oauthToken;
        headers.put("authorization", authorization);
        params.put("latitude", destination.getLatitude());
        params.put("longitude", destination.getLongitude());
        params.put("radius", "50");
        params.put("radiusUnit", "MILE");
        params.put("currency", "USD");

        String url = AMADEUS_HOTEL_URL;
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            httpBuider.addQueryParameter(param.getKey(), param.getValue());
        }
        url = httpBuider.build().toString();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();

        JsonHttpResponseHandler callback = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                HotelResponse hotelResponse = gson.fromJson(String.valueOf(json.jsonObject), HotelResponse.class);
                processHotels(hotelResponse);
                rlProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(HotelsActivity.this, "Unable to load hotels, please exit and try again", Toast.LENGTH_SHORT).show();
                rlProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "ERROR: ", throwable);
            }
        };
        okHttpClient.newCall(request).enqueue(callback);
    }

    private void processHotels(HotelResponse hotelResponse) {
        ArrayList<HotelList> hotelsList = hotelResponse.getData();
        for (HotelList listedOffer : hotelsList) {
            HotelInfo hotel = listedOffer.getHotel();
            String name = hotel.getName();
            String latitude = String.valueOf(hotel.getLatitude());
            String longitude = String.valueOf(hotel.getLongitude());
            Integer rating = hotel.getRating() == null ? 0 : Integer.parseInt(hotel.getRating());
            String address = hotel.getAddress() == null ? "N/A" : createAddress(hotel.getAddress());
            String phoneNumber = "N/A";
            String email = "N/A";
            if (hotel.getContact() != null) {
                phoneNumber = hotel.getContact().getPhone();
                email = hotel.getContact().getEmail();
            }
            String description = "N/A";
            if (hotel.getDescription() != null) {
                description = hotel.getDescription().getText();
            }
            ArrayList<HotelOffer> offers = processHotelOffers(listedOffer.getOffers());
            Hotel newHotel = new Hotel(name, address, latitude, longitude, phoneNumber, rating, email, description, offers);
            hotels.add(newHotel);
            markHotelOnMap(newHotel);
        }
    }

    private String createAddress(HotelAddress addressComponents) {
        String address = "";
        for (String line : addressComponents.getLines()) {
            address += line + ", ";
        }
        address += addressComponents.getCityName() == null ? "" : addressComponents.getCityName() + ", ";
        address += addressComponents.getCountryCode() == null ? "" : addressComponents.getCountryCode() + ", ";
        address += addressComponents.getPostalCode() == null ? "" : addressComponents.getPostalCode() + ", ";
        return address;
    }

    private ArrayList<HotelOffer> processHotelOffers(ArrayList<OfferInfo> hotelOffersList) {
        ArrayList<HotelOffer> offersList = new ArrayList<>();
        for (OfferInfo offer : hotelOffersList) {
            String checkInDate = offer.getCheckInDate() == null ? "N/A" : offer.getCheckInDate();
            String checkOutDate = offer.getCheckOutDate() == null ? "N/A" : offer.getCheckOutDate();
            String roomType = "N/A";
            Integer numBeds = 0;
            String description = "N/A";
            if (offer.getRoom() != null) {
                if (offer.getRoom().getTypeEstimated() != null) {
                    if (offer.getRoom().getTypeEstimated().getCategory() != null)
                        roomType = offer.getRoom().getTypeEstimated().getCategory();
                    if (offer.getRoom().getTypeEstimated().getBeds() != null) {
                        numBeds = offer.getRoom().getTypeEstimated().getBeds();
                    }
                }
                if (offer.getRoom().getDescription() != null) {
                    description = offer.getRoom().getDescription().getText();
                }
            }
            Integer numAdults = 0;
            if (offer.getGuest() != null && offer.getGuest().getAdults() != null) {
                numAdults = offer.getGuest().getAdults();
            }
            String cost = "N/A";
            if (offer.getPrice() != null && offer.getPrice().getTotal() != null) {
                cost = offer.getPrice().getTotal();
            }
            offersList.add(new HotelOffer(checkInDate, checkOutDate, roomType, numBeds, description, numAdults, cost));
        }
        return offersList;
    }

    private void markHotelOnMap(Hotel hotel) {
        MarkerOptions marker = new MarkerOptions().position(hotel.getCoords()).title(hotel.getName());
        map.addMarker(marker);
        markers.add(marker);
    }

    private void saveHotel(Hotel hotel) {
        currDestination.setHotelName(hotel.getName());
        currDestination.setHotelLat(hotel.getLatitude());
        currDestination.setHotelLong(hotel.getLongitude());
        currDestination.setHotelCost(hotel.getOffers().get(0).getCost());
        currDestination.setHotelAddress(hotel.getAddress());
        currDestination.setHotelPhone(hotel.getPhone());
        currDestination.setHotelDescription(hotel.getDescription());
        currDestination.setHotelEmail(hotel.getEmail());
        currDestination.setHotelRating(String.valueOf(hotel.getRating()));
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
            tvDescription.setText(getResources().getString(R.string.info_unavailable));
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
            if (chosenOffer.getNumBeds() == 0) {
                tvNumBeds.setText("N/A");
            } else {
                tvNumBeds.setText(String.valueOf(chosenOffer.getNumBeds()));
            }
            String money = "$" + chosenOffer.getCost();
            tvCost.setText(money);
        }
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

    class HotelResponse {
        private final ArrayList<HotelList> data = new ArrayList<HotelList>();

        public ArrayList<HotelList> getData() {
            return data;
        }
    }

    class HotelList {
        private final ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();
        private HotelInfo hotel;

        public HotelInfo getHotel() {
            return hotel;
        }

        public ArrayList<OfferInfo> getOffers() {
            return offers;
        }
    }

    class HotelInfo {
        private String name;
        private String rating;
        private Double latitude;
        private Double longitude;
        private HotelAddress address;
        private Contact contact;
        private Description description;

        public String getName() {
            return name;
        }

        public String getRating() {
            return rating;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public HotelAddress getAddress() {
            return address;
        }

        public Contact getContact() {
            return contact;
        }

        public Description getDescription() {
            return description;
        }
    }

    class HotelAddress {
        private final ArrayList<String> lines = new ArrayList<String>();
        private String postalCode;
        private String cityName;
        private String countryCode;

        public ArrayList<String> getLines() {
            return lines;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCityName() {
            return cityName;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    class Contact {
        private String phone;
        private String email;

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }
    }

    class Description {
        private String text;

        public String getText() {
            return text;
        }
    }

    class OfferInfo {
        private String checkInDate;
        private String checkOutDate;
        private Room room;
        private Guest guest;
        private OfferPrice price;

        public String getCheckInDate() {
            return checkInDate;
        }

        public String getCheckOutDate() {
            return checkOutDate;
        }

        public Room getRoom() {
            return room;
        }

        public Guest getGuest() {
            return guest;
        }

        public OfferPrice getPrice() {
            return price;
        }
    }

    class Room {
        private RoomType typeEstimated;
        private Description description;

        public RoomType getTypeEstimated() {
            return typeEstimated;
        }

        public Description getDescription() {
            return description;
        }
    }

    class RoomType {
        private String category;
        private Integer beds;

        public String getCategory() {
            return category;
        }

        public Integer getBeds() {
            return beds;
        }
    }

    class Guest {
        private Integer adults;

        public Integer getAdults() {
            return adults;
        }
    }

    class OfferPrice {
        private String total;

        public String getTotal() {
            return total;
        }
    }
}