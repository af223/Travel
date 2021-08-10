package com.codepath.travel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.fragments.AirportChosenFragment;
import com.codepath.travel.fragments.AirportSearchFragment;
import com.codepath.travel.models.Airport;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This adapter is for the RecylerView in AirportChosenFragment that displays the airports the user has chosen
 * from the search results list. If a user clicks the "remove" button next to an airport, the airport is
 * removed from the list, and immediately appears as addable in the search results list.
 */

public class ChosenAirportsAdapter extends RecyclerView.Adapter<ChosenAirportsAdapter.ViewHolder> {

    private final Context context;
    private final List<Airport> airports;

    public ChosenAirportsAdapter(Context context, List<Airport> airports) {
        this.context = context;
        this.airports = airports;
    }

    @NonNull
    @NotNull
    @Override
    public ChosenAirportsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_airport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChosenAirportsAdapter.ViewHolder holder, int position) {
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
            btnAddAirport.setText("Remove");
            btnAddAirport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    airports.remove(airport);
                    airport.flipChosen();
                    AirportChosenFragment.refreshChosenAirports();
                    AirportSearchFragment.refreshFoundAirports();
                }
            });
        }
    }
}
