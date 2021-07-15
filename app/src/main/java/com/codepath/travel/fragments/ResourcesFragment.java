package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.codepath.travel.FlightsActivity;
import com.codepath.travel.HotelsActivity;
import com.codepath.travel.R;
import com.codepath.travel.models.Destination;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResourcesFragment extends Fragment {

    private CardView toFlights;
    private CardView toHotels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resources, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        toFlights = view.findViewById(R.id.card_view_flights);
        toFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FlightsActivity.class);
                i.putExtra(Destination.KEY_OBJECT_ID, bundle.getString(Destination.KEY_OBJECT_ID));
                getContext().startActivity(i);
            }
        });

        toHotels = view.findViewById(R.id.card_view_hotels);
        toHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), HotelsActivity.class);
                i.putExtra(Destination.KEY_OBJECT_ID, bundle.getString(Destination.KEY_OBJECT_ID));
                getContext().startActivity(i);
            }
        });
    }
}