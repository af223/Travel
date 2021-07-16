package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.ChooseFlightActivity;
import com.codepath.travel.R;
import com.codepath.travel.models.Flight;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This adapter is for the RecylerView in ChooseFlightsActivity that displays the list of plane tickets
 * that originate from one of the chosen departure airports, and land in one of the destination/arrival airports.
 * Users can click on a ticket to select it, holding it on display at the top of the screen,
 * and click "confirm" to save that ticket, automatically redirected back to the FlightsActivity.java screen.
 */

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.ViewHolder> {

    private final Context context;
    private final List<Flight> flights;

    public FlightsAdapter(Context context, List<Flight> flights) {
        this.context = context;
        this.flights = flights;
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

        private final TextView tvDepartAirport;
        private final TextView tvArriveAirport;
        private final TextView tvCost;
        private final TextView tvAirline;
        private final TextView tvDate;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvDepartAirport = itemView.findViewById(R.id.tvDepartAirport);
            tvArriveAirport = itemView.findViewById(R.id.tvArriveAirport);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvAirline = itemView.findViewById(R.id.tvAirline);
            tvDate = itemView.findViewById(R.id.tvDate);
            itemView.setOnClickListener(this);
        }

        public void bind(Flight flight) {
            tvDepartAirport.setText(flight.getDepartAirportName());
            tvArriveAirport.setText(flight.getArriveAirportName());
            String formattedCost = "$" + flight.getFlightCost();
            tvCost.setText(formattedCost);
            tvAirline.setText(flight.getCarrier());
            tvDate.setText(flight.getDate());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ChooseFlightActivity.choose(flights.get(position));
            }
        }
    }
}
