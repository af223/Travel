package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

    private CardView toFlights;
    private CardView toHotels;
    private CardView toTouristSpots;

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

        toFlights = view.findViewById(R.id.card_view_flights);
        toFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, FlightsActivity.class);
            }
        });

        toHotels = view.findViewById(R.id.card_view_hotels);
        toHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, HotelsActivity.class);
            }
        });

        toTouristSpots = view.findViewById(R.id.card_view_activities);
        toTouristSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResourceActivity(bundle, TouristSpotsActivity.class);
            }
        });
    }

    public void startResourceActivity(Bundle bundle, Class resourceName) {
        Intent i = new Intent(getContext(), resourceName);
        i.putExtra(Destination.KEY_OBJECT_ID, bundle.getString(Destination.KEY_OBJECT_ID));
        i.putExtra(Destination.KEY_LAT, bundle.getString(Destination.KEY_LAT));
        i.putExtra(Destination.KEY_LONG, bundle.getString(Destination.KEY_LONG));
        getContext().startActivity(i);
    }
}