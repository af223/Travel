package com.codepath.travel.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.InboundFragment;
import com.codepath.travel.fragments.OutboundFragment;

import org.jetbrains.annotations.NotNull;

public class PagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OutboundFragment.newInstance();
            case 1:
                return InboundFragment.newInstance();
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
                return "Outbound Flights";
            case 1:
                return "Inbound Flights";
            default:
                return "";
        }
    }
}
