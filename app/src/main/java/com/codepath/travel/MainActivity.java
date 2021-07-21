package com.codepath.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travel.fragments.CostsFragment;
import com.codepath.travel.fragments.ItineraryFragment;
import com.codepath.travel.fragments.LocationsFragment;
import com.codepath.travel.fragments.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

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
                boolean addToBackStack = false;
                switch (item.getItemId()) {
                    case R.id.action_map:
                        fragment = new MapFragment();
                        break;
                    case R.id.action_locations:
                        getSupportActionBar().show();
                        fragment = new LocationsFragment();
                        addToBackStack = true;
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
                if (addToBackStack)
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                else
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int logout = R.id.logout;
        if (item.getItemId() == logout) {
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        if (item.getItemId() == android.R.id.home) {
            fragmentManager.popBackStackImmediate();
            //fragmentManager.beginTransaction().replace(R.id.flContainer, new LocationsFragment()).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}