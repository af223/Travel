package com.codepath.travel.models;

import android.content.Intent;

public class TouristSpot {
    private String businessName;
    private String rating;
    private String imageURL;
    private String yelpURL;
    private Boolean chosen;
    private String placeId;
    private Integer reviewCount;

    public TouristSpot(String businessName, String rating, String imageURL, String yelpURL, String placeId, Integer reviewCount) {
        this.businessName = businessName;
        this.rating = rating;
        this.imageURL = imageURL;
        this.yelpURL = yelpURL;
        this.chosen = false;
        this.placeId = placeId;
        this.reviewCount = reviewCount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getYelpURL() {
        return yelpURL;
    }

    public void setYelpURL(String yelpURL) {
        this.yelpURL = yelpURL;
    }

    public Boolean isChosen() {
        return chosen;
    }

    public void flipChosen() {
        chosen = !chosen;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
