package com.codepath.travel.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
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

    public static void processYelpResults(ArrayList<YelpData> data, JSONObject jsonObject, Context context) {
        try {
            JSONArray businesses = jsonObject.getJSONArray("businesses");
            for (int i = 0; i < businesses.length(); i++) {
                JSONObject business = businesses.getJSONObject(i);
                String name = business.getString("name");
                String rating = String.valueOf(business.getDouble("rating"));
                String imageURL = "";
                if (business.has("image_url")) {
                    imageURL = business.getString("image_url");
                }
                String yelpURL = business.getString("url");
                String phone = business.getString("phone");
                String address = formatBusinessAddress(business.getJSONObject("location"));
                String placeId = business.getString("id");
                Integer reviewCount = business.getInt("review_count");
                data.add(new YelpData(name, rating, imageURL, yelpURL, phone, address, placeId, reviewCount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to process Yelp results", Toast.LENGTH_SHORT).show();
        }
    }

    private static String formatBusinessAddress(JSONObject location) {
        String address = "";
        try {
            if (!location.getString("address1").isEmpty() && !location.getString("address1").equals("null"))
                address += location.getString("address1") + ", ";
            if (!location.getString("address2").isEmpty() && !location.getString("address2").equals("null"))
                address += location.getString("address2") + ", ";
            if (!location.getString("address3").isEmpty() && !location.getString("address3").equals("null"))
                address += location.getString("address3") + ", ";
            if (!location.getString("state").isEmpty() && !location.getString("state").equals("null"))
                address += location.getString("state") + ", ";
            if (!location.getString("city").isEmpty() && !location.getString("city").equals("null"))
                address += location.getString("city") + ", ";
            address += location.getString("country");
            address += " " + location.getString("zip_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                YelpData.processYelpResults(transportations, json.jsonObject, activity);
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
}
