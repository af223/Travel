package com.codepath.travel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travel.fragments.CostsFragment;
import com.codepath.travel.fragments.ItineraryFragment;
import com.codepath.travel.fragments.LocationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

/**
 * This activity holds the container for the main fragments to see destinations, choose them, see and plan
 * the itinerary, and see and edit the cost breakdown. It is started automatically from LoginActivity if the
 * user is logged in.
 */

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    public static void logout(Context context) {
        ParseUser.logOut();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_locations:
                        getSupportActionBar().show();
                        fragment = new LocationsFragment();
                        break;
                    case R.id.action_itinerary:
                        getSupportActionBar().show();
                        fragment = new ItineraryFragment();
                        break;
                    case R.id.action_costs:
                    default:
                        getSupportActionBar().show();
                        fragment = new CostsFragment();
                        break;
                }
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.anim)
                        .replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_locations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout(this);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            fragmentManager.popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
    }
}