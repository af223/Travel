package com.codepath.travel.models;

public class Flight {

    private String departAirportCode;
    private String departAirportName;
    private String arriveAirportCode;
    private String arriveAirportName;
    private String flightCost;
    private String carrier;
    private String date;
    private Boolean isDirect;

    public Flight(String departAirportCode, String departAirportName, String arriveAirportCode,
                  String arriveAirportName, String flightCost, String carrier, String date, Boolean isDirect) {
        this.departAirportCode = departAirportCode;
        this.departAirportName = departAirportName;
        this.arriveAirportCode = arriveAirportCode;
        this.arriveAirportName = arriveAirportName;
        this.flightCost = flightCost;
        this.carrier = carrier;
        this.date = date;
        this.isDirect = isDirect;
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

    public Boolean getDirect() {
        return isDirect;
    }

    public void setDirect(Boolean direct) {
        isDirect = direct;
    }
}
