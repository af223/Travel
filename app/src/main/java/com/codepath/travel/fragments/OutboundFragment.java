package com.codepath.travel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.FlightsActivity;
import com.codepath.travel.R;
import com.codepath.travel.adapters.FlightsAdapter;
import com.codepath.travel.models.Airport;
import com.codepath.travel.models.Flight;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * This fragment contains a list of the one-way plane tickets between the selected airports going to the destination. The tourist
 * can choose the plane ticket here. Confirming the ticket selection occurs in InboundFragment.
 */
public class OutboundFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "OutboundFragment";
    private ProgressBar pbFlights;
    private ArrayList<Flight> flights;
    private RecyclerView rvFlights;
    private FlightsAdapter adapter;
    private View ticket;
    private Spinner spinnerSortBy;

    public OutboundFragment() {
        // Required empty public constructor
    }

    public static OutboundFragment newInstance() {
        OutboundFragment fragment = new OutboundFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_outbound, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ticket = view.findViewById(R.id.card_view);
        pbFlights = view.findViewById(R.id.pbFlights);
        spinnerSortBy = view.findViewById(R.id.spinnerSortBy);

        flights = new ArrayList<>();
        rvFlights = view.findViewById(R.id.rvFlights);
        adapter = new FlightsAdapter(getContext(), flights, ticket, true);
        rvFlights.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFlights.setAdapter(adapter);

        Ticket outboundTicket = new Ticket(TAG, getActivity(), pbFlights);
        pbFlights.setVisibility(View.VISIBLE);
        for (Airport originAirport : FlightsActivity.departureAirports) {
            for (Airport destinationAirport : FlightsActivity.arrivalAirports) {
                outboundTicket.getFlights(originAirport.getIATACode(), destinationAirport.getIATACode(), adapter, flights);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Ticket.sortMethods);
        spinnerSortBy.setAdapter(adapter);
        spinnerSortBy.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Ticket.onSortTickets(adapter, rvFlights, flights, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}