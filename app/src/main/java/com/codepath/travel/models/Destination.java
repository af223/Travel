package com.codepath.travel.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Destination")
public class Destination extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";
    public static final String KEY_ADMIN1 = "admin_area_1";
    public static final String KEY_ADMIN2 = "admin_area_2";
    public static final String KEY_SUBLOCAL = "sublocality";
    public static final String KEY_LOCAL = "locality";
    public static final String KEY_COUNTRY = "country";

    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getLatitude() {
        return getString(KEY_LAT);
    }

    public void setLatitude(String latitude) {
        put(KEY_LAT, latitude);
    }

    public String getLongitude() {
        return getString(KEY_LONG);
    }

    public void setLongitude(String longitude) {
        put(KEY_LONG, longitude);
    }

    public String getAdminArea1() {
        return getString(KEY_ADMIN1);
    }

    public void setAdminArea1(String area1) {
        put(KEY_ADMIN1, area1);
    }

    public String getAdminArea2() {
        return getString(KEY_ADMIN2);
    }

    public void setAdminArea2(String area2) {
        put(KEY_ADMIN2, area2);
    }

    public String getSublocality() {
        return getString(KEY_SUBLOCAL);
    }

    public void setSublocality(String sublocality) {
        put(KEY_SUBLOCAL, sublocality);
    }

    public String getLocality() {
        return getString(KEY_LOCAL);
    }

    public void setLocality(String locality) {
        put(KEY_LOCAL, locality);
    }

    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    public LatLng getCoords() {
        return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
    }

    public String getFormattedLocationName() {
        String result = "";
        String local = getLocality();
        String area1 = getAdminArea1();
        String country = getCountry();
        if (local != null) {
            result += local + ", ";
        }
        if (area1 != null) {
            result += area1 + ", ";
        }
        if (country != null) {
            result += country;
        }
        return result;
    }
}
