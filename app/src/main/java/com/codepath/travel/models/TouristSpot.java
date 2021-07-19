package com.codepath.travel.models;

public class TouristSpot {
    private String businessName;
    private String rating;
    private String imageURL;
    private String yelpURL;

    public TouristSpot(String businessName, String rating, String imageURL, String yelpURL) {
        this.businessName = businessName;
        this.rating = rating;
        this.imageURL = imageURL;
        this.yelpURL = yelpURL;
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
}
