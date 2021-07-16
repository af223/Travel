package com.codepath.travel.models;

public class HotelOffer {

    private String checkInDate;
    private String checkOutDate;
    private String roomType;
    private Integer numBeds;
    private String description;
    private Integer numAdults;
    private String cost;

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

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getNumBeds() {
        return numBeds;
    }

    public void setNumBeds(Integer numBeds) {
        this.numBeds = numBeds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(Integer numAdults) {
        this.numAdults = numAdults;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
