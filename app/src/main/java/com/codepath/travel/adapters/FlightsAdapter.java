package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.fragments.InboundFragment;
import com.codepath.travel.fragments.Ticket;
import com.codepath.travel.models.Flight;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This adapter is for the RecylerView in InboundFragment and OutboundFragment that displays the list of plane tickets
 * that originate from one of the chosen departure airports, and land in one of the destination/arrival airports.
 * Users can click on a ticket to select it, holding it on display at the top of the screen,
 * and click "confirm" to save that ticket, automatically redirected back to the FlightsActivity.java screen.
 */

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {

    private final Context context;
    private final List<Flight> flights;
    private final View ticket;
    private final Boolean outbound;

    public FlightsAdapter(Context context, List<Flight> flights, View ticket, Boolean outbound) {
        this.context = context;
        this.flights = flights;
        this.ticket = ticket;
        this.outbound = outbound;
    }

    @NonNull
    @NotNull
    @Override
    public FlightsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FlightsAdapter.ViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.bind(flight);
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

        public void bind(Flight flight) {
            Ticket.displayTicket(flight, view);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                InboundFragment.choose(flights.get(position), ticket, outbound, context);
            }
        }
    }
}
