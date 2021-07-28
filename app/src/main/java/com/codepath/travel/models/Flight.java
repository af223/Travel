package com.codepath.travel.models;

import org.parceler.Parcel;

@Parcel
public class Flight {

    public String departAirportCode;
    public String departAirportName;
    public String arriveAirportCode;
    public String arriveAirportName;
    public String flightCost;
    public String carrier;
    public String date;
    public Boolean isRoundtrip;

    public Flight() {
    }

    public Flight(String departAirportCode, String departAirportName, String arriveAirportCode,
                  String arriveAirportName, String flightCost, String carrier, String date, Boolean isRoundtrip) {
        this.departAirportCode = departAirportCode;
        this.departAirportName = departAirportName;
        this.arriveAirportCode = arriveAirportCode;
        this.arriveAirportName = arriveAirportName;
        this.flightCost = flightCost;
        this.carrier = carrier;
        this.date = date;
        this.isRoundtrip = isRoundtrip;
    }

    public String getDepartAirportCode() {
        return departAirportCode;
    }

    public void setDepartAirportCode(String departAirportCode) {
        this.departAirportCode = departAirportCode;
    }

    public String getDepartAirportName() {
        return departAirportName;
    }

    public void setDepartAirportName(String departAirportName) {
        this.departAirportName = departAirportName;
    }

    public String getArriveAirportCode() {
        return arriveAirportCode;
    }

    public void setArriveAirportCode(String arriveAirportCode) {
        this.arriveAirportCode = arriveAirportCode;
    }

    public String getArriveAirportName() {
        return arriveAirportName;
    }

    public void setArriveAirportName(String arriveAirportName) {
        this.arriveAirportName = arriveAirportName;
    }

    public String getFlightCost() {
        return flightCost;
    }

    public void setFlightCost(String flightCost) {
        this.flightCost = flightCost;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDirect(Boolean direct) {
    }

    public Boolean isRoundtrip() {
        return isRoundtrip;
    }

    public void setRoundtrip(Boolean isRoundtrip) {
        this.isRoundtrip = isRoundtrip;
    }
}
