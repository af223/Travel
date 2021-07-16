package com.codepath.travel.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Hotel {

    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String phone;
    private Integer rating;
    private String email;
    private String description;
    private ArrayList<HotelOffer> offers;

    public Hotel(String name, String address, String latitude, String longitude, String phone, Integer rating, String email, String description, ArrayList<HotelOffer> offers) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.rating = rating;
        this.email = email;
        this.description = description;
        this.offers = offers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<HotelOffer> getOffers() {
        return offers;
    }

    public LatLng getCoords() {
        return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
    }
}
