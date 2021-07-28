package com.codepath.travel;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travel.fragments.ChosenTicketsFragment;
import com.codepath.travel.models.Destination;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import static com.codepath.travel.MainActivity.logout;

/**
 * This activity displays everything that the user has selected in their trip.
 * <p>
 * This activity is started when the user clicks on "See All Plans" from an expanded location item
 * in LocationsFragment.
 */

public class AllPlansActivity extends AppCompatActivity {

    private static final String TAG = "AllPlansActivity";
    private static FragmentManager fragmentManager;
    private Toolbar toolbar;
    private CardView cvHotel;
    private TextView tvPickedHotel;
    private TextView tvHotelName;
    private TextView tvHotelCost;
    private RatingBar rbRating;
    private TextView tvHotelPhone;
    private TextView tvHotelEmail;
    private TextView tvHotelAddress;
    private TextView tvHotelDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_plans);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Plans");

        bindViews();

        fragmentManager = getSupportFragmentManager();
        loadDestination();
    }

    private void bindViews() {
        cvHotel = findViewById(R.id.card_view_hotel);
        tvPickedHotel = findViewById(R.id.tvPickedHotel);
        tvHotelName = findViewById(R.id.tvHotelName);
        tvHotelCost = findViewById(R.id.tvHotelCost);
        rbRating = findViewById(R.id.rbRating);
        tvHotelPhone = findViewById(R.id.tvHotelPhone);
        tvHotelEmail = findViewById(R.id.tvHotelEmail);
        tvHotelAddress = findViewById(R.id.tvHotelAddress);
        tvHotelDescription = findViewById(R.id.tvHotelDescription);
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
                displayHotel(destination);
            }
        });
    }

    private void displayHotel(Destination destination) {
        if (destination.getHotelName() == null) {
            cvHotel.setVisibility(View.GONE);
            tvPickedHotel.setText(R.string.no_hotel);
        } else {
            tvPickedHotel.setText(R.string.chosen_hotel);
            tvHotelName.setText(destination.getHotelName());
            tvHotelCost.setText("$" + destination.getHotelCost());
            rbRating.setRating(Integer.parseInt(destination.getHotelRating()));
            tvHotelPhone.setText(destination.getHotelPhone());
            tvHotelEmail.setText(destination.getHotelEmail());
            tvHotelAddress.setText(destination.getHotelAddress());
            tvHotelDescription.setText(destination.getHotelDescription());
        }
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
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}