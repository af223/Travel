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
    public static final String KEY_DEPART_CODE = "departAirportCode";
    public static final String KEY_DEPART_NAME = "departAirportName";
    public static final String KEY_ARRIVE_CODE = "arriveAirportCode";
    public static final String KEY_ARRIVE_NAME = "arriveAirportName";
    public static final String KEY_COST = "flightCost";
    public static final String KEY_CARRIER = "carrier";
    public static final String KEY_DATE = "date";
    public static final String KEY_HOTEL_NAME = "hotelName";
    public static final String KEY_HOTEL_LAT = "hotelLatitude";
    public static final String KEY_HOTEL_LONG = "hotelLongitude";
    public static final String KEY_HOTEL_COST = "hotelCost";
    public static final String KEY_HOTEL_ADDRESS = "hotelAddress";
    public static final String KEY_HOTEL_PHONE = "hotelPhone";
    public static final String KEY_HOTEL_DESCRIPTION = "hotelDescription";
    public static final String KEY_INBOUND_DEPART_CODE = "inboundDepartCode";
    public static final String KEY_INBOUND_DEPART_NAME = "inboundDepartName";
    public static final String KEY_INBOUND_ARRIVE_CODE = "inboundArriveCode";
    public static final String KEY_INBOUND_ARRIVE_NAME = "inboundArriveName";
    public static final String KEY_INBOUND_COST = "inboundFlightCost";
    public static final String KEY_INBOUND_CARRIER = "inboundCarrier";
    public static final String KEY_INBOUND_DATE = "inboundDate";
    public static final String KEY_IS_ROUNDTRIP = "isRoundtrip";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
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

    public String getDepartAirportCode() {
        return getString(KEY_DEPART_CODE);
    }

    public void setDepartAirportCode(String code) {
        put(KEY_DEPART_CODE, code);
    }

    public String getDepartAirportName() {
        return getString(KEY_DEPART_NAME);
    }

    public void setDepartAirportName(String name) {
        put(KEY_DEPART_NAME, name);
    }

    public String getArriveAirportCode() {
        return getString(KEY_ARRIVE_CODE);
    }

    public void setArriveAirportCode(String code) {
        put(KEY_ARRIVE_CODE, code);
    }

    public String getArriveAirportName() {
        return getString(KEY_ARRIVE_NAME);
    }

    public void setArriveAirportName(String name) {
        put(KEY_ARRIVE_NAME, name);
    }

    public String getCost() {
        return getString(KEY_COST);
    }

    public void setCost(String cost) {
        put(KEY_COST, cost);
    }

    public String getCarrier() {
        return getString(KEY_CARRIER);
    }

    public void setCarrier(String carrier) {
        put(KEY_CARRIER, carrier);
    }

    public String getDate() {
        return getString(KEY_DATE);
    }

    public void setDate(String date) {
        put(KEY_DATE, date);
    }

    public String getHotelName() {
        return getString(KEY_HOTEL_NAME);
    }

    public void setHotelName(String name) {
        put(KEY_HOTEL_NAME, name);
    }

    public String getHotelLat() {
        return getString(KEY_HOTEL_LAT);
    }

    public void setHotelLat(String latitude) {
        put(KEY_HOTEL_LAT, latitude);
    }

    public String getHotelLong() {
        return getString(KEY_HOTEL_LONG);
    }

    public void setHotelLong(String longitude) {
        put(KEY_HOTEL_LONG, longitude);
    }

    public String getHotelCost() {
        return getString(KEY_HOTEL_COST);
    }

    public void setHotelCost(String cost) {
        put(KEY_HOTEL_COST, cost);
    }

    public String getHotelAddress() {
        return getString(KEY_HOTEL_ADDRESS);
    }

    public void setHotelAddress(String address) {
        put(KEY_HOTEL_ADDRESS, address);
    }

    public String getHotelPhone() {
        return getString(KEY_HOTEL_PHONE);
    }

    public void setHotelPhone(String phone) {
        put(KEY_HOTEL_PHONE, phone);
    }

    public String getHotelDescription() {
        return getString(KEY_HOTEL_DESCRIPTION);
    }

    public void setHotelDescription(String description) {
        put(KEY_HOTEL_DESCRIPTION, description);
    }

    public String getInboundDepartCode() {
        return getString(KEY_INBOUND_DEPART_CODE);
    }

    public void setInboundDepartCode(String code) {
        put(KEY_INBOUND_DEPART_CODE, code);
    }

    public String getInboundDepartName() {
        return getString(KEY_INBOUND_DEPART_NAME);
    }

    public void setInboundDepartName(String name) {
        put(KEY_INBOUND_DEPART_NAME, name);
    }

    public String getInboundArriveCode() {
        return getString(KEY_INBOUND_ARRIVE_CODE);
    }

    public void setInboundArriveCode(String code) {
        put(KEY_INBOUND_ARRIVE_CODE, code);
    }

    public String getInboundArriveName() {
        return getString(KEY_INBOUND_ARRIVE_NAME);
    }

    public void setInboundArriveName(String name) {
        put(KEY_INBOUND_ARRIVE_NAME, name);
    }

    public String getInboundCost() {
        return getString(KEY_INBOUND_COST);
    }

    public void setInboundCost(String cost) {
        put(KEY_INBOUND_COST, cost);
    }

    public String getInboundCarrier() {
        return getString(KEY_INBOUND_CARRIER);
    }

    public void setInboundCarrier(String carrier) {
        put(KEY_INBOUND_CARRIER, carrier);
    }

    public String getInboundDate() {
        return getString(KEY_INBOUND_DATE);
    }

    public void setInboundDate(String date) {
        put(KEY_INBOUND_DATE, date);
    }

    public Boolean isRoundtrip() {
        return getBoolean(KEY_IS_ROUNDTRIP);
    }

    public void setIsRoundtrip(Boolean isRoundtrip) {
        put(KEY_IS_ROUNDTRIP, isRoundtrip);
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
