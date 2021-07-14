package com.codepath.travel.models;

public class Airport {

    private String name;
    private String IATACode;
    private String country;
    private Boolean chosen;

    public Airport(String name, String IATACode, String country) {
        this.name = name;
        this.IATACode = IATACode;
        this.country = country;
        this.chosen = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIATACode() {
        return IATACode;
    }

    public void setIATACode(String IATACode) {
        this.IATACode = IATACode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean isChosen() {
        return chosen;
    }

    public void flipChosen() {
        this.chosen = !this.chosen;
    }
}
