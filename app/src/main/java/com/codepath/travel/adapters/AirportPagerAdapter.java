package com.codepath.travel.adapters;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.AirportChosenFragment;
import com.codepath.travel.fragments.AirportSearchFragment;

import org.jetbrains.annotations.NotNull;

public class AirportPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    private final Intent intent;
    private final Boolean isFromDeparture;

    public AirportPagerAdapter(FragmentManager fragmentManager, Intent intent, Boolean isFromDeparture) {
        super(fragmentManager);
        this.intent = intent;
        this.isFromDeparture = isFromDeparture;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AirportSearchFragment(intent, isFromDeparture);
            case 1:
                return new AirportChosenFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Find Airports";
            case 1:
                return "Chosen Airports";
            default:
                return "";
        }
    }
}
