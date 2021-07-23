package com.codepath.travel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.codepath.travel.adapters.PagerAdapter;


/**
 * This activity is a container for the fragments displaying one-way tickets between the chosen airports.
 * The user can choose a ticket by clicking on it, then clicking the confirm
 * button, at which point they're directed back to FlightsActivity.java and the data is stored on Parse.
 * <p>
 * This activity appears when the user clicks the "see one-way flights" buttons from FlightsActivity.java.
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
    }
}