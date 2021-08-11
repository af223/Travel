package com.codepath.travel.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.models.YelpData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;

import okhttp3.Headers;

/**
 * This activity displays the details of the selected business along with three Yelp reviews.
 * <p>
 * This activity is launched when the user clicks on either a transportation, tourist destination, or
 * restaurants.
 */

public class YelpDetailsActivity extends AppCompatActivity {

    private static final String TAG = "YelpDetailsActivity";
    private static final String YELP_BUSINESS_DETAILS_URL = "https://api.yelp.com/v3/businesses/%1$s";
    private static final String YELP_BUSINESS_REVIEWS_URL = "https://api.yelp.com/v3/businesses/%1$s/reviews";
    private String placeId;
    private Gson gson;
    private ImageView ivBackdrop;
    private TextView tvYelpName;
    private ImageView ivYelpRatingBar;
    private ImageView ivYelpLogo;
    private TextView tvYelpNumRatings;
    private TextView tvCurrentlyOpen;
    private TextView tvYelpPrice;
    private TextView tvYelpPhone;
    private TextView tvYelpAddress;
    private View reviewOne;
    private View reviewTwo;
    private View reviewThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_details);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        placeId = getIntent().getStringExtra(getResources().getString(R.string.place_id));

        ivBackdrop = findViewById(R.id.ivBackdrop);
        tvYelpName = findViewById(R.id.tvYelpName);
        ivYelpRatingBar = findViewById(R.id.ivYelpRatingBar);
        ivYelpLogo = findViewById(R.id.ivYelpLogo);
        tvYelpNumRatings = findViewById(R.id.tvYelpNumRatings);
        tvCurrentlyOpen = findViewById(R.id.tvCurrentlyOpen);
        tvYelpPrice = findViewById(R.id.tvYelpPrice);
        tvYelpPhone = findViewById(R.id.tvYelpPhone);
        tvYelpAddress = findViewById(R.id.tvYelpAddress);
        reviewOne = findViewById(R.id.reviewOne);
        reviewTwo = findViewById(R.id.reviewTwo);
        reviewThree = findViewById(R.id.reviewThree);

        loadTouristDetails();
        loadTouristReviews();
    }

    private void loadTouristDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(String.format(YELP_BUSINESS_DETAILS_URL, placeId), headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                BusinessDetails businessDetails = gson.fromJson(String.valueOf(json.jsonObject), BusinessDetails.class);
                displayDetails(businessDetails);
                if (businessDetails.getHours() != null)
                    displayBusinessHours(businessDetails.getHours().get(0).getOpen());
                else
                    hideBusinessHours();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(YelpDetailsActivity.this, "Unable to find business details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTouristReviews() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + getResources().getString(R.string.yelp_api_key));
        client.get(String.format(YELP_BUSINESS_REVIEWS_URL, placeId), headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                BusinessReviews businessReviews = gson.fromJson(String.valueOf(json.jsonObject), BusinessReviews.class);
                displayReviews(businessReviews);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(YelpDetailsActivity.this, "Unable to find business reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDetails(BusinessDetails businessDetails) {
        Glide.with(this).load(businessDetails.getImage_url()).placeholder(R.drawable.no_photo_placeholder).into(ivBackdrop);
        tvYelpName.setText(businessDetails.getName());
        YelpData.displayRatingBar(ivYelpRatingBar, String.valueOf(businessDetails.getRating()), this);
        ivYelpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(businessDetails.getUrl()));
                YelpDetailsActivity.this.startActivity(i);
            }
        });
        String numRatings = businessDetails.getReview_count() + " reviews";
        tvYelpNumRatings.setText(numRatings);
        if (businessDetails.getHours() != null && businessDetails.getHours().get(0).getIs_open_now()) {
            tvCurrentlyOpen.setText(R.string.open);
            tvCurrentlyOpen.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvCurrentlyOpen.setText(R.string.closed);
            tvCurrentlyOpen.setTextColor(getResources().getColor(R.color.quantum_googred));
        }
        if (businessDetails.getPrice() != null && !businessDetails.getPrice().isEmpty()) {
            tvYelpPrice.setVisibility(View.VISIBLE);
            tvYelpPrice.setText(businessDetails.getPrice());
        } else {
            tvYelpPrice.setVisibility(View.GONE);
        }
        if (businessDetails.getDisplay_phone() != null && !businessDetails.getDisplay_phone().isEmpty()) {
            tvYelpPhone.setText(businessDetails.getDisplay_phone());
            tvYelpPhone.setVisibility(View.VISIBLE);
        } else if (businessDetails.getPhone() != null && !businessDetails.getPhone().isEmpty()) {
            tvYelpPhone.setText(businessDetails.getPhone());
            tvYelpPhone.setVisibility(View.VISIBLE);
        } else {
            tvYelpPhone.setVisibility(View.GONE);
        }
        if (businessDetails.getLocation().getDisplay_address().size() > 0) {
            String address = businessDetails.getLocation().getDisplay_address().get(0);
            for (int i = 1; i < businessDetails.getLocation().getDisplay_address().size(); i++) {
                address += ", " + businessDetails.getLocation().getDisplay_address().get(i);
            }
            tvYelpAddress.setText(address);
        } else {
            tvYelpAddress.setText(getResources().getString(R.string.n_a));
        }
    }


    private void displayBusinessHours(ArrayList<DailyHours> open) {
        for (DailyHours hours : open) {
            TextView tvDay = findViewById(R.id.tvMondayHours);
            switch (hours.getDay()) {
                case 1:
                    tvDay = findViewById(R.id.tvTuesdayHours);
                    break;
                case 2:
                    tvDay = findViewById(R.id.tvWednesdayHours);
                    break;
                case 3:
                    tvDay = findViewById(R.id.tvThursdayHours);
                    break;
                case 4:
                    tvDay = findViewById(R.id.tvFridayHours);
                    break;
                case 5:
                    tvDay = findViewById(R.id.tvSaturdayHours);
                    break;
                case 6:
                    tvDay = findViewById(R.id.tvSundayHours);
                    break;
            }
            String timeRange = hours.getStart().substring(0, 2) + ":" + hours.getStart().substring(2, 4) + " - " +
                    hours.getEnd().substring(0, 2) + ":" + hours.getEnd().substring(2, 4);
            if (tvDay.getText().toString().isEmpty()) {
                tvDay.setText(timeRange);
            } else {
                timeRange = tvDay.getText().toString() + ", " + timeRange;
                tvDay.setText(timeRange);
            }
        }
        TextView todayDay = findViewById(R.id.tvSun);
        TextView todayHours = findViewById(R.id.tvSundayHours);
        switch (LocalDate.now().getDayOfWeek()) {
            case MONDAY:
                todayDay = findViewById(R.id.tvMon);
                todayHours = findViewById(R.id.tvMondayHours);
                break;
            case TUESDAY:
                todayDay = findViewById(R.id.tvTues);
                todayHours = findViewById(R.id.tvTuesdayHours);
                break;
            case WEDNESDAY:
                todayDay = findViewById(R.id.tvWed);
                todayHours = findViewById(R.id.tvWednesdayHours);
                break;
            case THURSDAY:
                todayDay = findViewById(R.id.tvThurs);
                todayHours = findViewById(R.id.tvThursdayHours);
                break;
            case FRIDAY:
                todayDay = findViewById(R.id.tvFri);
                todayHours = findViewById(R.id.tvFridayHours);
                break;
            case SATURDAY:
                todayDay = findViewById(R.id.tvSat);
                todayHours = findViewById(R.id.tvSaturdayHours);
                break;

        }
        todayDay.setTypeface(null, Typeface.BOLD);
        todayHours.setTypeface(null, Typeface.BOLD);
    }

    private void hideBusinessHours() {
        findViewById(R.id.tvBusinessHours).setVisibility(View.GONE);
        findViewById(R.id.tvMon).setVisibility(View.GONE);
        findViewById(R.id.tvMondayHours).setVisibility(View.GONE);
        findViewById(R.id.tvTues).setVisibility(View.GONE);
        findViewById(R.id.tvTuesdayHours).setVisibility(View.GONE);
        findViewById(R.id.tvWed).setVisibility(View.GONE);
        findViewById(R.id.tvWednesdayHours).setVisibility(View.GONE);
        findViewById(R.id.tvThurs).setVisibility(View.GONE);
        findViewById(R.id.tvThursdayHours).setVisibility(View.GONE);
        findViewById(R.id.tvFri).setVisibility(View.GONE);
        findViewById(R.id.tvFridayHours).setVisibility(View.GONE);
        findViewById(R.id.tvSat).setVisibility(View.GONE);
        findViewById(R.id.tvSaturdayHours).setVisibility(View.GONE);
        findViewById(R.id.tvSun).setVisibility(View.GONE);
        findViewById(R.id.tvSundayHours).setVisibility(View.GONE);
    }

    private void displayReviews(BusinessReviews businessReviews) {
        if (businessReviews.getTotal() == 0) {
            reviewOne.setVisibility(View.GONE);
            reviewTwo.setVisibility(View.GONE);
            reviewThree.setVisibility(View.GONE);
        } else if (businessReviews.getTotal() == 1) {
            reviewOne.setVisibility(View.VISIBLE);
            showReview(reviewOne, businessReviews.getReviews().get(0));
            reviewTwo.setVisibility(View.GONE);
            reviewThree.setVisibility(View.GONE);
        } else if (businessReviews.getTotal() == 2) {
            reviewOne.setVisibility(View.VISIBLE);
            showReview(reviewOne, businessReviews.getReviews().get(0));
            reviewTwo.setVisibility(View.VISIBLE);
            showReview(reviewTwo, businessReviews.getReviews().get(1));
            reviewThree.setVisibility(View.GONE);
        } else {
            reviewOne.setVisibility(View.VISIBLE);
            showReview(reviewOne, businessReviews.getReviews().get(0));
            reviewTwo.setVisibility(View.VISIBLE);
            showReview(reviewTwo, businessReviews.getReviews().get(1));
            reviewThree.setVisibility(View.VISIBLE);
            showReview(reviewThree, businessReviews.getReviews().get(2));
        }
    }

    private void showReview(View view, Review review) {
        ImageView ivPicture = view.findViewById(R.id.ivPicture);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        ImageView ivUserRating = view.findViewById(R.id.ivUserRating);
        TextView tvRatingDate = view.findViewById(R.id.tvRatingDate);
        TextView tvReview = view.findViewById(R.id.tvReview);

        if (review.getUser().getImage_url() != null && !review.getUser().getImage_url().isEmpty()) {
            Glide.with(this).load(review.getUser().getImage_url()).placeholder(R.drawable.no_photo_placeholder).transform(new CircleCrop()).into(ivPicture);
        } else {
            Glide.with(this).load(R.drawable.no_photo_placeholder).transform(new CircleCrop()).into(ivPicture);
        }
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(review.getUser().getProfile_url()));
                YelpDetailsActivity.this.startActivity(i);
            }
        });
        tvUsername.setText(review.getUser().getName());
        YelpData.displayRatingBar(ivUserRating, String.valueOf(review.getRating()), this);
        tvRatingDate.setText(review.getTime_created().substring(0, 10));
        tvReview.setText(review.getText());
    }

    class BusinessDetails {
        private String name;
        private String image_url;
        private String url;
        private String phone;
        private String display_phone;
        private Integer review_count;
        private Double rating;
        private BusinessLocation location;
        private String price;
        private final ArrayList<BusinessHours> hours = new ArrayList<BusinessHours>();
        private final ArrayList<SpecialHours> special_hours = new ArrayList<>();

        public String getName() {
            return name;
        }

        public String getImage_url() {
            return image_url;
        }

        public String getUrl() {
            return url;
        }

        public String getPhone() {
            return phone;
        }

        public String getDisplay_phone() {
            return display_phone;
        }

        public Integer getReview_count() {
            return review_count;
        }

        public Double getRating() {
            return rating;
        }

        public BusinessLocation getLocation() {
            return location;
        }

        public String getPrice() {
            return price;
        }

        public ArrayList<BusinessHours> getHours() {
            return hours;
        }

        public ArrayList<SpecialHours> getSpecial_hours() {
            return special_hours;
        }
    }

    class BusinessLocation {
        private final ArrayList<String> display_address = new ArrayList<>();

        public ArrayList<String> getDisplay_address() {
            return display_address;
        }
    }

    class BusinessHours {
        private final ArrayList<DailyHours> open = new ArrayList<DailyHours>();
        private Boolean is_open_now;

        public ArrayList<DailyHours> getOpen() {
            return open;
        }

        public Boolean getIs_open_now() {
            return is_open_now;
        }
    }

    class DailyHours {
        private String start;
        private String end;
        private Integer day;

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public Integer getDay() {
            return day;
        }
    }

    class SpecialHours {
        private String date;
        private Boolean is_closed;
        private String start;
        private String end;

        public String getDate() {
            return date;
        }

        public Boolean getIs_closed() {
            return is_closed;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }

    class BusinessReviews {
        private final ArrayList<Review> reviews = new ArrayList<Review>();
        private Integer total;

        public ArrayList<Review> getReviews() {
            return reviews;
        }

        public Integer getTotal() {
            return total;
        }
    }

    class Review {
        private String id;
        private Double rating;
        private Reviewer user;
        private String text;
        private String time_created;
        private String url;

        public String getId() {
            return id;
        }

        public Double getRating() {
            return rating;
        }

        public Reviewer getUser() {
            return user;
        }

        public String getText() {
            return text;
        }

        public String getTime_created() {
            return time_created;
        }

        public String getUrl() {
            return url;
        }
    }

    class Reviewer {
        private String profile_url;
        private String image_url;
        private String name;

        public String getProfile_url() {
            return profile_url;
        }

        public String getImage_url() {
            return image_url;
        }

        public String getName() {
            return name;
        }
    }
}