package com.codepath.travel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travel.fragments.ChosenTicketsFragment;
import com.codepath.travel.models.Destination;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * This activity displays everything that the user has selected in their trip.
 * <p>
 * This activity is started when the user clicks on "See All Plans" from an expanded location item
 * in LocationsFragment.
 */

public class AllPlansActivity extends AppCompatActivity {

    private static final String TAG = "AllPlansActivity";
    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_plans);

        fragmentManager = getSupportFragmentManager();
        loadDestination();
    }

    private void loadDestination() {
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.getInBackground(getIntent().getStringExtra(Destination.KEY_OBJECT_ID), new GetCallback<Destination>() {
            @Override
            public void done(Destination destination, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to load destination", e);
                    Toast.makeText(AllPlansActivity.this, "Unable to load destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                Fragment fragment = new ChosenTicketsFragment(destination);
                fragmentManager.beginTransaction().replace(R.id.flTickets, fragment).commit();
            }
        });
    }
}