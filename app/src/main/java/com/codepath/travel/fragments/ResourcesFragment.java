package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.codepath.travel.FlightsActivity;
import com.codepath.travel.HotelsActivity;
import com.codepath.travel.R;
import com.codepath.travel.TouristSpotsActivity;
import com.codepath.travel.models.Destination;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * This fragment contains a list of options that the user can choose to plan for, like the flight and hotel.
 * This fragment is reached when the user selects a destination from the LocationsFragment, and clicking
 * on a resource in this fragment starts [resource]Activity (like FlightsActivity.java).
 */
public class ResourcesFragment extends Fragment {

    private TextView tvFlights;
    private TextView tvHotels;
    private TextView tvTouristSpots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resources, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Resources");

        tvFlights = view.findViewById(R.id.tvFlights);
        tvFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, FlightsActivity.class, tvFlights, "to_flights_transition");
            }
        });

        tvHotels = view.findViewById(R.id.tvHotels);
        tvHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, HotelsActivity.class, tvHotels, "to_hotels_transition");
            }
        });

        tvTouristSpots = view.findViewById(R.id.tvTouristSpots);
        tvTouristSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, TouristSpotsActivity.class, tvTouristSpots, "to_tourist_spots_transition");
            }
        });
    }

    public void startResourceActivity(Bundle bundle, Class resourceName, View sharedView, String name) {
        Intent i = new Intent(getContext(), resourceName);
        i.putExtra(Destination.KEY_OBJECT_ID, bundle.getString(Destination.KEY_OBJECT_ID));
        i.putExtra(Destination.KEY_LAT, bundle.getString(Destination.KEY_LAT));
        i.putExtra(Destination.KEY_LONG, bundle.getString(Destination.KEY_LONG));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), sharedView, name);
        getContext().startActivity(i, options.toBundle());
    }
}