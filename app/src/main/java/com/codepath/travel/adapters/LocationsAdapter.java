package com.codepath.travel.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.FlightsActivity;
import com.codepath.travel.HotelsActivity;
import com.codepath.travel.R;
import com.codepath.travel.TouristSpotsActivity;
import com.codepath.travel.TransportationActivity;
import com.codepath.travel.models.Destination;

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
        private final ImageView ivFlightExpanded;
        private final ImageView ivHotelExpanded;
        private final ExpandableLayout expandableLayout;
        private final LinearLayout llFlight;
        private final LinearLayout llHotel;
        private final LinearLayout llTouristSpot;
        private final LinearLayout llTransportation;
        private final ImageView ivArrow;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cvLocation = itemView.findViewById(R.id.cvLocation);
            cvLocation.setOnClickListener(this);
            tvName = itemView.findViewById(R.id.tvName);
            ivGotPlaneTicket = itemView.findViewById(R.id.ivGotPlaneTicket);
            ivGotHotel = itemView.findViewById(R.id.ivGotHotel);
            ivFlightExpanded = itemView.findViewById(R.id.ivFlightExpanded);
            ivHotelExpanded = itemView.findViewById(R.id.ivHotelExpanded);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            llFlight = itemView.findViewById(R.id.llFlight);
            llHotel = itemView.findViewById(R.id.llHotel);
            llTouristSpot = itemView.findViewById(R.id.llTouristSpot);
            llTransportation = itemView.findViewById(R.id.llTransportation);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }

        public void bind(Destination destination) {
            tvName.setText(destination.getFormattedLocationName());
            if (destination.getDate() != null && destination.getInboundDate() != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotPlaneTicket.getDrawable()), context.getResources().getColor(R.color.dark_green));
                DrawableCompat.setTint(DrawableCompat.wrap(ivFlightExpanded.getDrawable()), context.getResources().getColor(R.color.dark_green));
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotPlaneTicket.getDrawable()), context.getResources().getColor(R.color.gray));
                DrawableCompat.setTint(DrawableCompat.wrap(ivFlightExpanded.getDrawable()), context.getResources().getColor(R.color.gray));
            }
            if (destination.getHotelName() != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotHotel.getDrawable()), context.getResources().getColor(R.color.dark_green));
                DrawableCompat.setTint(DrawableCompat.wrap(ivHotelExpanded.getDrawable()), context.getResources().getColor(R.color.dark_green));
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(ivGotHotel.getDrawable()), context.getResources().getColor(R.color.gray));
                DrawableCompat.setTint(DrawableCompat.wrap(ivHotelExpanded.getDrawable()), context.getResources().getColor(R.color.gray));
            }
            expandableLayout.setExpanded(false);
            llFlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(FlightsActivity.class, destination);
                }
            });
            llHotel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(HotelsActivity.class, destination);
                }
            });
            llTouristSpot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(TouristSpotsActivity.class, destination);
                }
            });
            llTransportation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startResourceActivity(TransportationActivity.class, destination);
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
