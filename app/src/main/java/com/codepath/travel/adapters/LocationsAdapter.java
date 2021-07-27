package com.codepath.travel.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.FlightsActivity;
import com.codepath.travel.HotelsActivity;
import com.codepath.travel.R;
import com.codepath.travel.TouristSpotsActivity;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Expense;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This adapter is for the RecylerView in LocationsFragment that displays the list of destinations that
 * the user wants to plan to visit. Clicking on an item (one of the locations) expands it, displaying
 * a list of (clickable) resources that take the user to the respective page.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private final Context context;
    private final List<Destination> locations;

    public LocationsAdapter(Context context, List<Destination> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @NotNull
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LocationsAdapter.ViewHolder holder, int position) {
        Destination destination = locations.get(position);
        holder.bind(destination);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

        private final CardView cvLocation;
        private final TextView tvName;
        private final ImageView ivGotPlaneTicket;
        private final ImageView ivGotHotel;
        private final ExpandableLayout expandableLayout;
        private final TextView tvFlights;
        private final TextView tvHotels;
        private final TextView tvTouristSpots;
        private final ImageView ivArrow;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cvLocation = itemView.findViewById(R.id.cvLocation);
            cvLocation.setOnClickListener(this);
            tvName = itemView.findViewById(R.id.tvName);
            ivGotPlaneTicket = itemView.findViewById(R.id.ivGotPlaneTicket);
            ivGotHotel = itemView.findViewById(R.id.ivGotHotel);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            tvFlights = itemView.findViewById(R.id.tvFlights);
            tvHotels = itemView.findViewById(R.id.tvHotels);
            tvTouristSpots = itemView.findViewById(R.id.tvTouristSpots);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }

        public void bind(Destination destination) {
            tvName.setText(destination.getFormattedLocationName());
            if (destination.getDate() != null && destination.getInboundDate() != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotPlaneTicket.getDrawable()), context.getResources().getColor(R.color.dark_green));
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotPlaneTicket.getDrawable()), context.getResources().getColor(R.color.gray));
            }
            if (destination.getHotelName() != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotHotel.getDrawable()), context.getResources().getColor(R.color.dark_green));
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotHotel.getDrawable()), context.getResources().getColor(R.color.gray));
            }
            expandableLayout.setExpanded(false);
            tvFlights.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(FlightsActivity.class, destination);
                }
            });
            tvHotels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(HotelsActivity.class, destination);
                }
            });
            tvTouristSpots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(TouristSpotsActivity.class, destination);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                expandableLayout.toggle();
            }
        }

        private void startResourceActivity(Class resourceName, Destination destination) {
            Intent i = new Intent(context, resourceName);
            i.putExtra(Destination.KEY_OBJECT_ID, destination.getObjectId());
            i.putExtra(Destination.KEY_LAT, destination.getLatitude());
            i.putExtra(Destination.KEY_LONG, destination.getLongitude());
            context.startActivity(i);
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDED) {
                ivArrow.setRotation(90);
            } else if (state == ExpandableLayout.State.COLLAPSED) {
                ivArrow.setRotation(270);
            }
        }
    }
}
