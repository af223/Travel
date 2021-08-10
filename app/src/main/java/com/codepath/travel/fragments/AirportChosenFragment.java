package com.codepath.travel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travel.R;
import com.codepath.travel.activities.ChooseAirportsActivity;
import com.codepath.travel.adapters.ChosenAirportsAdapter;
import com.codepath.travel.models.Airport;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * This fragment is displayed by the AirportPagerAdapter in the ChooseAirportsActivity
 * to allow users to find and save airports using a search.
 */
public class AirportChosenFragment extends Fragment {

    private static final String TAG = "AirportChosenFragment";
    private static ChosenAirportsAdapter chosenAdapter;
    private RecyclerView rvChosenAirports;
    private Button btnClearChosen;

    public AirportChosenFragment() {
    }

    public static void refreshChosenAirports() {
        chosenAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airport_chosen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnClearChosen = view.findViewById(R.id.btnClearChosen);
        btnClearChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Airport airport : ChooseAirportsActivity.chosenAirportsList) {
                    airport.flipChosen();
                }
                ChooseAirportsActivity.chosenAirportsList.clear();
                refreshChosenAirports();
                AirportSearchFragment.refreshFoundAirports();
            }
        });

        chosenAdapter = new ChosenAirportsAdapter(getContext(), ChooseAirportsActivity.chosenAirportsList);
        rvChosenAirports = view.findViewById(R.id.rvChosenAirports);
        rvChosenAirports.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChosenAirports.setAdapter(chosenAdapter);
    }
}