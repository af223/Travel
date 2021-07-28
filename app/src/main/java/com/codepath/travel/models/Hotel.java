package com.codepath.travel.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Hotel {

    private final String name;
    private final String address;
    private final String latitude;
    private final String longitude;
    private final String phone;
    private final Integer rating;
    private final String email;
    private final String description;
    private final ArrayList<HotelOffer> offers;

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

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getRating() {
        return rating;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<HotelOffer> getOffers() {
        return offers;
    }

    public LatLng getCoords() {
        return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
    }
}
