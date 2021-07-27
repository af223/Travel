package com.codepath.travel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.travel.R;
import com.codepath.travel.models.Destination;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * This fragment displays the chosen flight tickets (if they have been chosen). It appears in the
 * FlightsActivity screen, and also in the AllPlansActivity.
 */
public class ChosenTicketsFragment extends Fragment {

    private Destination destination;
    private View chosenOutboundFlight;
    private View chosenInboundFlight;
    private TextView tvFlightType;

    public ChosenTicketsFragment() {
        // Required empty public constructor
    }

    public ChosenTicketsFragment(Destination destination) {
        this.destination = destination;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chosen_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chosenOutboundFlight = view.findViewById(R.id.chosenOutboundFlight);
        chosenInboundFlight = view.findViewById(R.id.chosenInboundFlight);
        tvFlightType = view.findViewById(R.id.tvFlightType);
        loadChosenFlights();
    }

    private void loadChosenFlights() {
        if (destination.getDate() == null) {
            chosenOutboundFlight.setVisibility(View.INVISIBLE);
        } else {
            setFlightType();
            chosenOutboundFlight.setVisibility(View.VISIBLE);
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvDepartAirport)).setText(destination.getDepartAirportName());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvArriveAirport)).setText(destination.getArriveAirportName());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvCost)).setText("$" + destination.getCost());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvAirline)).setText(destination.getCarrier());
            ((TextView) chosenOutboundFlight.findViewById(R.id.tvDate)).setText(destination.getDate());
        }
        if (destination.getInboundDate() == null) {
            chosenInboundFlight.setVisibility(View.INVISIBLE);
        } else {
            setFlightType();
            chosenInboundFlight.setVisibility(View.VISIBLE);
            ((TextView) chosenInboundFlight.findViewById(R.id.tvDepartAirport)).setText(destination.getInboundDepartName());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvArriveAirport)).setText(destination.getInboundArriveName());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvCost)).setText("$" + destination.getInboundCost());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvAirline)).setText(destination.getInboundCarrier());
            ((TextView) chosenInboundFlight.findViewById(R.id.tvDate)).setText(destination.getInboundDate());
        }
    }

    private void setFlightType() {
        if (destination.isRoundtrip()) {
            tvFlightType.setText(R.string.is_roundtrip);
            chosenInboundFlight.findViewById(R.id.tvCost).setVisibility(View.INVISIBLE);
        } else {
            tvFlightType.setText(R.string.is_one_way);
            chosenInboundFlight.findViewById(R.id.tvCost).setVisibility(View.VISIBLE);
        }
    }
}