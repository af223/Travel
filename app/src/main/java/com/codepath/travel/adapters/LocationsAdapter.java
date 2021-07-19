package com.codepath.travel.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.MainActivity;
import com.codepath.travel.R;
import com.codepath.travel.fragments.LocationsFragment;
import com.codepath.travel.fragments.ResourcesFragment;
import com.codepath.travel.models.Destination;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This adapter is for the RecylerView in LocationsFragment that displays the list of destinations that
 * the user wants to plan to visit. Clicking on an item (one of the locations) takes them to ResourcesFragment.
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);
        }

        public void bind(Destination destination) {
            tvName.setText(destination.getFormattedLocationName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Fragment fragment = new ResourcesFragment();
                Bundle bundle = new Bundle();
                Destination destination = locations.get(position);
                safeAddToBundle(destination, Destination.KEY_LOCAL, bundle);
                safeAddToBundle(destination, Destination.KEY_ADMIN1, bundle);
                safeAddToBundle(destination, Destination.KEY_ADMIN2, bundle);
                safeAddToBundle(destination, Destination.KEY_COUNTRY, bundle);
                bundle.putString(Destination.KEY_OBJECT_ID, destination.getObjectId());
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        }

        private void safeAddToBundle(Destination destination, String key, Bundle bundle) {
            if (destination.getString(key) != null) {
                bundle.putString(key, destination.getString(key));
            } else {
                bundle.putString(key, "");
            }
        }
    }
}
