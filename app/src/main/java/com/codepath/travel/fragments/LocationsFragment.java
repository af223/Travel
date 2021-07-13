package com.codepath.travel.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.LocationsAdapter;
import com.codepath.travel.models.Destination;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationsFragment";
    private RecyclerView rvLocations;
    public static FragmentManager locationsFragManager;
    private List<Destination> locations;
    private LocationsAdapter adapter;

    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationsFragManager = getChildFragmentManager();
        locations = new ArrayList<>();
        adapter = new LocationsAdapter(getContext(), locations);
        rvLocations = view.findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLocations.setAdapter(adapter);
        loadLocations();
    }

    private void loadLocations() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.include(Destination.KEY_USER);
        query.addAscendingOrder(Destination.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> destinations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to load locations", e);
                    Toast.makeText(getContext(), "Unable to load locations", Toast.LENGTH_SHORT).show();
                    return;
                }
                locations.addAll(destinations);
                adapter.notifyDataSetChanged();
            }
        });
    }
}