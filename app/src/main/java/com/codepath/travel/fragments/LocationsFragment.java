package com.codepath.travel.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.activities.MainActivity;
import com.codepath.travel.adapters.LocationsAdapter;
import com.codepath.travel.adapters.RecyclerTouched;
import com.codepath.travel.models.Destination;
import com.codepath.travel.models.TouristDestination;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * This fragment displays the list of locations that the user plans to visit. The user navigates to this
 * fragment by clicking on the pin/marker icon in the bottom navigation, and it is the automatic starting fragment
 * when the app is first opened. Clicking on a location here expands into a list of resources that start
 * the activities for the respective resource.
 */
public class LocationsFragment extends Fragment {

    private static final String TAG = "LocationsFragment";
    public static FragmentManager locationsFragManager;
    public static List<Destination> locations;
    private static LocationsAdapter adapter;
    private RecyclerView rvLocations;
    private FloatingActionButton fabAddLocation;
    private ProgressBar pbLoadDestinations;
    private RecyclerTouched rvTouchListener;

    public LocationsFragment() {
        // Required empty public constructor
    }

    public static void addLocation(Destination destination) {
        locations.add(destination);
        adapter.notifyItemInserted(locations.size() - 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Choose a location");

        pbLoadDestinations = view.findViewById(R.id.pbLoadDestinations);
        pbLoadDestinations.setVisibility(View.VISIBLE);
        fabAddLocation = view.findViewById(R.id.fabAddLocation);
        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MapFragment();
                MainActivity.fragmentManager.beginTransaction().addSharedElement(fabAddLocation, "to_map_transition").replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        locationsFragManager = getChildFragmentManager();
        locations = new ArrayList<>();
        adapter = new LocationsAdapter(getContext(), locations);
        rvLocations = view.findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLocations.setAdapter(adapter);
        rvTouchListener = new RecyclerTouched(getActivity(), rvLocations);
        rvTouchListener.setSwipeOptionViews(R.id.delete_task, R.id.edit_entry).setSwipeable(R.id.cvLocation, R.id.swipeMenuLayout, new RecyclerTouched.OnSwipeOptionsClickListener() {
            @Override
            public void onSwipeOptionClicked(int viewID, int position) {
                if (viewID == R.id.delete_task) {
                    deleteDestination(position);
                }
            }
        });
        rvLocations.addOnItemTouchListener(rvTouchListener);

        loadLocations();
    }

    private void loadLocations() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.whereEqualTo(Destination.KEY_USER, ParseUser.getCurrentUser());
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
                pbLoadDestinations.setVisibility(View.GONE);
            }
        });
    }

    private void deleteDestination(int position) {
        Destination toDelete = locations.get(position);
        ParseQuery<TouristDestination> query = ParseQuery.getQuery(TouristDestination.class);
        query.whereEqualTo(TouristDestination.KEY_DESTINATION, toDelete);
        query.findInBackground(new FindCallback<TouristDestination>() {
            @Override
            public void done(List<TouristDestination> touristDestinations, ParseException e) {
                for (TouristDestination activity : touristDestinations) {
                    activity.deleteInBackground();
                }
            }
        });
        toDelete.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                locations.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }
}