package com.codepath.travel.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.travel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CostsFragment extends Fragment {

    public CostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_costs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: set up view for summary of costs of user's trip
        // probably using Recycler View
        super.onViewCreated(view, savedInstanceState);
    }
}