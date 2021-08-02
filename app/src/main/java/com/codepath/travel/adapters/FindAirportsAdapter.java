package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.activities.AirportSearchActivity;
import com.codepath.travel.R;
import com.codepath.travel.models.Airport;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is for the RecylerView in AirportSearchActivity that displays the search results of airports
 * based on the user's query (or the automatically loaded suggested arrival airports).
 * If a user clicks the "add" button next to an airport, the airport appears in the chosen airports list,
 * and the "add" button is no longer clickable, displaying "added".
 */

public class FindAirportsAdapter extends RecyclerView.Adapter<FindAirportsAdapter.ViewHolder> {

    private final Context context;
    private final List<Airport> airports;
    private final ArrayList<Airport> chosenAirports;

    public FindAirportsAdapter(Context context, List<Airport> airports, ArrayList<Airport> chosenAirports) {
        this.context = context;
        this.airports = airports;
        this.chosenAirports = chosenAirports;
    }

    @NonNull
    @NotNull
    @Override
    public FindAirportsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_airport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindAirportsAdapter.ViewHolder holder, int position) {
        Airport airport = airports.get(position);
        holder.bind(airport);
    }

    @Override
    public int getItemCount() {
        return airports.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAirportName;
        private final TextView tvAirportCountry;
        private final Button btnAddAirport;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvAirportName = itemView.findViewById(R.id.tvAirportName);
            tvAirportCountry = itemView.findViewById(R.id.tvAirportCountry);
            btnAddAirport = itemView.findViewById(R.id.btnAddAirport);
        }

        public void bind(Airport airport) {
            tvAirportName.setText(airport.getName());
            tvAirportCountry.setText(airport.getCountry());
            if (airport.isChosen()) {
                btnAddAirport.setText("Added");
                btnAddAirport.setBackgroundColor(context.getResources().getColor(R.color.quantum_grey));
                btnAddAirport.setClickable(false);
            } else {
                btnAddAirport.setClickable(true);
                btnAddAirport.setText("Add");
                btnAddAirport.setBackgroundColor(context.getResources().getColor(R.color.lightest_pink));
                btnAddAirport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chosenAirports.add(airport);
                        btnAddAirport.setText("Added");
                        btnAddAirport.setBackgroundColor(context.getResources().getColor(R.color.quantum_grey));
                        airport.flipChosen();
                        AirportSearchActivity.refreshChosenAirports();
                        btnAddAirport.setClickable(false);
                    }
                });
            }
        }
    }
}
