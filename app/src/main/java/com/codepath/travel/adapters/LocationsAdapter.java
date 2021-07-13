package com.codepath.travel.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.MainActivity;
import com.codepath.travel.R;
import com.codepath.travel.fragments.ResourcesFragment;
import com.codepath.travel.models.Destination;


import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                if (destination.getLocality() != null) {
                    bundle.putString(Destination.KEY_LOCAL, destination.getLocality());
                } else {
                    bundle.putString(Destination.KEY_LOCAL, "");
                }
                if (destination.getAdminArea1() != null) {
                    bundle.putString(Destination.KEY_ADMIN1, destination.getAdminArea1());
                } else {
                    bundle.putString(Destination.KEY_ADMIN1, "");
                }
                if (destination.getLocality() != null) {
                    bundle.putString(Destination.KEY_ADMIN2, destination.getAdminArea2());
                } else {
                    bundle.putString(Destination.KEY_ADMIN2, "");
                }
                if (destination.getLocality() != null) {
                    bundle.putString(Destination.KEY_COUNTRY, destination.getCountry());
                } else {
                    bundle.putString(Destination.KEY_COUNTRY, "");
                }
                bundle.putString(Destination.KEY_OBJECT_ID, destination.getObjectId());
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        }
    }
}
