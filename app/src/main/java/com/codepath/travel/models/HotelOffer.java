package com.codepath.travel.models;

public class HotelOffer {

    private final String checkInDate;
    private final String checkOutDate;
    private final String roomType;
    private final Integer numBeds;
    private final String description;
    private final Integer numAdults;
    private final String cost;

    public HotelOffer(String checkInDate, String checkOutDate, String roomType, Integer numBeds, String description, Integer numAdults, String cost) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.numBeds = numBeds;
        this.description = description;
        this.numAdults = numAdults;
        this.cost = cost;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getRoomType() {
        return roomType;
    }

    public Integer getNumBeds() {
        return numBeds;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNumAdults() {
        return numAdults;
    }

    public String getCost() {
        return cost;
    }
}
