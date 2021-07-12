package com.codepath.travel.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.travel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragment extends Fragment {

    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: set up view for locations chosen by user
        // probably using Recycler View
        super.onViewCreated(view, savedInstanceState);
    }
}