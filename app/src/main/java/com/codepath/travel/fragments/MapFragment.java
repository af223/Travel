package com.codepath.travel.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.models.Destination;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.Headers;

public class MapFragment extends Fragment {

    private static final String revGeocodeURL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String TAG = "MapsFragment";
    private TextView tvLocation;
    private JSONObject chosenLocation;
    private static GoogleMap map;
    private AutocompleteSupportFragment autocompleteFragment;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    reverseGeocode(latLng);
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getResources().getString(R.string.maps_api_key));
        }
        autocompleteFragment =
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG));
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                reverseGeocode(place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "An error occurred: " + status);
                if (status.isCanceled()) {
                    Toast.makeText(getContext(), "No location entered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unable to choose location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reverseGeocode(LatLng coords) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("latlng", formatLatlng(coords));
        params.put("key", getResources().getString(R.string.maps_api_key));
        params.put("location_type", "APPROXIMATE");
        params.put("language", "en");
        client.get(revGeocodeURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    chosenLocation = results.getJSONObject(0);
                    String address = chosenLocation.getString("formatted_address");
                    showDestinationAlertDialog(coords, address);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Unable to find location", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(getContext(), "Unable to find a location", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Geocode API call failed", throwable);
                Log.e(TAG, "status code: " + statusCode);
                Log.e(TAG, response);
            }
        });
    }

    private void showDestinationAlertDialog(final LatLng latLng, String address) {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.map_message_item, null);
        tvLocation = messageView.findViewById(R.id.tvLocation);
        tvLocation.setText(address);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(latLng.toString())
                                .icon(defaultMarker));
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        saveDestination(chosenLocation, latLng);
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });
        alertDialog.show();
    }

    private void saveDestination(JSONObject chosenLocation, LatLng coords) {
        Destination dest = new Destination();
        dest.setUser(ParseUser.getCurrentUser());
        dest.setLatitude(String.valueOf(coords.latitude));
        dest.setLongitude(String.valueOf(coords.longitude));
        try {
            JSONArray components = chosenLocation.getJSONArray("address_components");
            for (int i = 0; i < components.length(); i++) {
                JSONObject place = components.getJSONObject(i);
                JSONArray types = place.getJSONArray("types");
                for(int j = 0; j < types.length(); j++) {
                    switch (types.getString(j)) {
                        case "administrative_area_level_1":
                            dest.setAdminArea1(place.getString("long_name"));
                            break;
                        case "administrative_area_level_2":
                            dest.setAdminArea2(place.getString("long_name"));
                            break;
                        case "sublocality":
                            dest.setSublocality(place.getString("long_name"));
                            break;
                        case "locality":
                            dest.setLocality(place.getString("long_name"));
                            break;
                        case "country":
                            dest.setCountry(place.getString("short_name"));
                            break;
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse JSON file");
            e.printStackTrace();
        }
        dest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving location", e);
                    Toast.makeText(getContext(), "Unable to select location", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "Location chosen!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatLatlng(LatLng coords) {
        return String.valueOf(coords.latitude) + "," + String.valueOf(coords.longitude);
    }
}