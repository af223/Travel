package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.RoundtripFlightsActivity;
import com.codepath.travel.models.Flight;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * This adapter is for the RecylerView in RoundtripFlightsActivity that displays the list of roundtrip plane tickets
 * that originate from one of the chosen departure airports, and land in one of the destination/arrival airports.
 * Users can click on a ticket to select it, holding it on display at the top of the screen,
 * and click "confirm" to save that ticket, automatically redirected back to the FlightsActivity.java screen.
 */

public class RoundtripsAdapter extends RecyclerView.Adapter<RoundtripsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Pair<Flight, Flight>> flights;

    public RoundtripsAdapter(Context context, ArrayList<Pair<Flight, Flight>> flights) {
        this.context = context;
        this.flights = flights;
    }

    @NonNull
    @NotNull
    @Override
    public RoundtripsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_roundtrip_flight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoundtripsAdapter.ViewHolder holder, int position) {
        Pair<Flight, Flight> flightPair = flights.get(position);
        holder.bind(flightPair);
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View view;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            view = itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(Pair<Flight, Flight> flightPair) {
            RoundtripFlightsActivity.displayTicket(flightPair, view);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                RoundtripFlightsActivity.chooseRoundtrip(flights.get(position));
            }
        }
    }
}
