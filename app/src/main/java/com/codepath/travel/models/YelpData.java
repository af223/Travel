package com.codepath.travel.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

public class YelpData {

    private static final String YELP_BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final Integer NUM_LOAD_BUSINESSES = 25;
    private final String businessName;
    private final String rating;
    private final String imageURL;
    private final String yelpURL;
    private final String phone;
    private final String address;
    private final String businessID;
    private final Integer reviewCount;
    private Boolean isChosen;

    public YelpData(String businessName, String rating, String imageURL, String yelpURL, String phone, String address, String businessID, Integer reviewCount) {
        this.businessName = businessName;
        this.rating = rating;
        this.imageURL = imageURL;
        this.yelpURL = yelpURL;
        this.phone = phone;
        this.address = address;
        this.businessID = businessID;
        this.reviewCount = reviewCount;
        this.isChosen = false;
    }

    public static void displayRatingBar(ImageView ivRatingBar, String rate, Context context) {
        int image = R.drawable.stars_extra_large_0;
        switch (rate) {
            case "1.0":
                image = R.drawable.stars_extra_large_1;
                break;
            case "1.5":
                image = R.drawable.stars_extra_large_1_half;
                break;
            case "2.0":
                image = R.drawable.stars_extra_large_2;
                break;
            case "2.5":
                image = R.drawable.stars_extra_large_2_half;
                break;
            case "3.0":
                image = R.drawable.stars_extra_large_3;
                break;
            case "3.5":
                image = R.drawable.stars_extra_large_3_half;
                break;
            case "4.0":
                image = R.drawable.stars_extra_large_4;
                break;
            case "4.5":
                image = R.drawable.stars_extra_large_4_half;
                break;
            case "5.0":
                image = R.drawable.stars_extra_large_5;
                break;
        }
        Glide.with(context).load(image).into(ivRatingBar);
    }

    public static void linkToYelp(String yelpURL, ImageButton ibYelp, Context context) {
        if (yelpURL == null) {
            ibYelp.setVisibility(View.GONE);
            return;
        }
        ibYelp.setVisibility(View.VISIBLE);
        ibYelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(yelpURL));
                context.startActivity(i);
            }
        });
    }

    public static void processYelpResults(ArrayList<YelpData> data, JSONObject jsonObject) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        YelpResponse yelpResponse = gson.fromJson(String.valueOf(jsonObject), YelpResponse.class);
        ArrayList<Business> businesses = yelpResponse.getBusinesses();
        for (Business business : businesses) {
            String name = business.getName();
            String rating = String.valueOf(business.getRating());
            String imageURL = business.getImage_url() == null ? "" : business.getImage_url();
            String yelpURL = business.getUrl();
            String phone = business.getPhone();
            String address = formatBusinessAddress(business.getLocation());
            String placeId = business.getId();
            Integer reviewCount = business.getReview_count();
            data.add(new YelpData(name, rating, imageURL, yelpURL, phone, address, placeId, reviewCount));
        }
    }

    private static String formatBusinessAddress(BusinessAddress location) {
        String address = "";
        if (location.getAddress1() != null && !location.getAddress1().isEmpty())
            address += location.getAddress1() + ", ";
        if (location.getAddress2() != null && !location.getAddress2().isEmpty())
            address += location.getAddress2() + ", ";
        if (location.getAddress3() != null && !location.getAddress3().isEmpty())
            address += location.getAddress3() + ", ";
        if (location.getState() != null && !location.getState().isEmpty())
            address += location.getState() + ", ";
        if (location.getCity() != null && !location.getCity().isEmpty())
            address += location.getAddress1() + ", ";
        address += location.getCountry();
        address += " " + location.getZip_code();
        return address;
    }

    public static void saveTouristDestination(YelpData touristSpot, Destination destination, Boolean isRestaurant) {
        TouristDestination touristDestination = new TouristDestination();
        touristDestination.setUser(ParseUser.getCurrentUser());
        touristDestination.setPlaceId(touristSpot.getBusinessID());
        touristDestination.setName(touristSpot.getBusinessName());
        touristDestination.setDestination(destination);
        touristDestination.setImageURL(touristSpot.getImageURL());
        touristDestination.setYelpURL(touristSpot.getYelpURL());
        touristDestination.setRating(touristSpot.getRating());
        touristDestination.setCommentCount(touristSpot.getReviewCount());
        touristDestination.setIsRestaurant(isRestaurant);
        touristDestination.saveInBackground();
    }

    public static RequestParams createRequestParams(String latitude, String longitude, String categoryParameter, String keywordQuery, int offset) {
        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        if (!categoryParameter.isEmpty()) {
            params.put("categories", categoryParameter);
        }
        if (!keywordQuery.isEmpty()) {
            params.put("term", keywordQuery);
        }
        params.put("limit", NUM_LOAD_BUSINESSES);
        params.put("offset", offset);
        return params;
    }

    public static void loadDataFromYelp(RequestParams params, ArrayList<YelpData> transportations, Activity activity,
                                        Runnable transportRunnable) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + activity.getResources().getString(R.string.yelp_api_key));
        client.get(YELP_BUSINESS_SEARCH_URL, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                YelpData.processYelpResults(transportations, json.jsonObject);
                activity.runOnUiThread(transportRunnable);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(activity, "Unable to find tourist activities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getNumLoadBusiness() {
        return NUM_LOAD_BUSINESSES;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRating() {
        return rating;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getYelpURL() {
        return yelpURL;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessID() {
        return businessID;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public Boolean isChosen() {
        return isChosen;
    }

    public void flipChosen() {
        isChosen = !isChosen;
    }

    class YelpResponse {
        private final ArrayList<Business> businesses = new ArrayList<Business>();

        public ArrayList<Business> getBusinesses() {
            return businesses;
        }
    }

    class Business {
        private String name;
        private Double rating;
        private String image_url;
        private String url;
        private String phone;
        private BusinessAddress location;
        private String id;
        private Integer review_count;

        public String getName() {
            return name;
        }

        public Double getRating() {
            return rating;
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

        public BusinessAddress getLocation() {
            return location;
        }

        public String getId() {
            return id;
        }

        public Integer getReview_count() {
            return review_count;
        }
    }

    class BusinessAddress {
        private String city;
        private String country;
        private String address1;
        private String address2;
        private String address3;
        private String state;
        private String zip_code;

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getAddress1() {
            return address1;
        }

        public String getAddress2() {
            return address2;
        }

        public String getAddress3() {
            return address3;
        }

        public String getState() {
            return state;
        }

        public String getZip_code() {
            return zip_code;
        }
    }
}
