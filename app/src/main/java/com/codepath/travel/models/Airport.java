package com.codepath.travel.models;

public class Airport {

    private final String name;
    private final String IATACode;
    private final String country;
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

    public String getIATACode() {
        return IATACode;
    }

    public String getCountry() {
        return country;
    }

    public Boolean isChosen() {
        return chosen;
    }

    public void flipChosen() {
        this.chosen = !this.chosen;
    }
}
