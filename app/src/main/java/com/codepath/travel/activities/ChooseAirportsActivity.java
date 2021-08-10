package com.codepath.travel.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.codepath.travel.R;
import com.codepath.travel.adapters.AirportPagerAdapter;
import com.codepath.travel.models.Airport;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * This airport contains the ViewPager that allows users to find and select airports they want to
 * later see flights for. This activity is launched from FlightsActivity when the user clicks
 * on see departure/arrival airports.
 */

public class ChooseAirportsActivity extends AppCompatActivity {

    public static ArrayList<Airport> chosenAirportsList;
    private static FragmentPagerAdapter adapterViewPager;
    private Boolean isFromDeparture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_airports);

        isFromDeparture = getIntent().getBooleanExtra(getResources().getString(R.string.from_departure), true);

        if (isFromDeparture) {
            chosenAirportsList = FlightsActivity.DEPARTURE_AIRPORTS;
        } else {
            chosenAirportsList = FlightsActivity.ARRIVAL_AIRPORTS;
        }

        ViewPager vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new AirportPagerAdapter(getSupportFragmentManager(), getIntent(), isFromDeparture);
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
    }
}