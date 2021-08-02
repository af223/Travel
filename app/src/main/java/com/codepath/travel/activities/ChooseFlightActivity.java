package com.codepath.travel.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.codepath.travel.R;
import com.codepath.travel.adapters.PagerAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * This activity is a container for the fragments displaying one-way tickets (inbound/outbound) between the chosen airports.
 * The user can choose a ticket by clicking on it, then clicking the confirm
 * button, at which point they're directed back to FlightsActivity.java and the data is stored on Parse.
 * <p>
 * This activity appears when the user clicks the "see one-way tickets" buttons from FlightsActivity.java.
 */

public class ChooseFlightActivity extends AppCompatActivity {

    private static FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flight);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
    }
}