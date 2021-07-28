package com.codepath.travel.fragments;

import android.content.Intent;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * This fragment contains a list of the one-way plane tickets between the selected airports returning from the destination. The tourist
 * can choose the plane ticket here. This fragment contains the button to confirm choosing the ticket.
 */
public class InboundFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "InboundFragment";
    private ProgressBar pbFlights;
    private Dictionary<Integer, String> placesCode;
    private Dictionary<Integer, String> placesName;
    private Dictionary<Integer, String> carriers;
    private ArrayList<Flight> flights;
    private RecyclerView rvFlights;
    private FlightsAdapter adapter;
    private View ticket;
    private Spinner spinnerSortBy;

    public InboundFragment() {
        // Required empty public constructor
    }

    public static InboundFragment newInstance() {
        InboundFragment fragment = new InboundFragment();
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
        return inflater.inflate(R.layout.fragment_inbound, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ticket = view.findViewById(R.id.card_view);
        pbFlights = view.findViewById(R.id.pbFlights);
        spinnerSortBy = view.findViewById(R.id.spinnerSortBy);
        Ticket.btnConfirm = view.findViewById(R.id.btnConfirm);
        Ticket.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                getActivity().setResult(RESULT_OK, intent);
                if (Ticket.chosenOutboundFlight != null) {
                    intent.putExtra(getString(R.string.outbound), Parcels.wrap(Ticket.chosenOutboundFlight));
                }
                if (Ticket.chosenInboundFlight != null) {
                    intent.putExtra(getString(R.string.inbound), Parcels.wrap(Ticket.chosenInboundFlight));
                }
                Ticket.chosenInboundFlight = null;
                Ticket.chosenOutboundFlight = null;
                getActivity().finish();
            }
        });

        placesCode = new Hashtable<>();
        placesName = new Hashtable<>();
        carriers = new Hashtable<>();
        flights = new ArrayList<>();

        rvFlights = view.findViewById(R.id.rvFlights);
        adapter = new FlightsAdapter(getContext(), flights, ticket, false);
        rvFlights.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFlights.setAdapter(adapter);

        pbFlights.setVisibility(View.VISIBLE);
        for (Airport originAirport : FlightsActivity.departureAirports) {
            for (Airport destinationAirport : FlightsActivity.arrivalAirports) {
                Ticket.getFlights(destinationAirport.getIATACode(), originAirport.getIATACode(), getActivity(),
                        pbFlights, adapter, TAG, placesCode, placesName, carriers, flights);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Ticket.sortMethods);
        spinnerSortBy.setAdapter(adapter);
        spinnerSortBy.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (Ticket.sortMethods[position]) {
            case "Cost":
                Collections.sort(flights, Ticket.compareCost);
                break;
            case "Departure Date":
                Collections.sort(flights, Ticket.compareDate);
                break;
            case "Airline":
                Collections.sort(flights, Ticket.compareAirline);
                break;
        }
        adapter.notifyDataSetChanged();
        rvFlights.smoothScrollToPosition(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}