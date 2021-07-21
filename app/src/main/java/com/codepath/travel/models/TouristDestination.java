package com.codepath.travel.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("TouristDestination")
public class TouristDestination extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_DESTINATION = "destination";
    public static final String KEY_PLACEID = "placeId";
    public static final String KEY_DATE_VISIT = "dateVisited";
    public static final String KEY_NAME = "businessName";
    public static final String KEY_TIME_VISIT = "timeVisited";
    public static final String KEY_VISIT_LENGTH = "visitLength";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Destination getDestination() {
        return (Destination) getParseObject(KEY_DESTINATION);
    }

    public void setDestination(Destination destination) {
        put(KEY_DESTINATION, destination);
    }

    public String getPlaceId() {
        return getString("placeId");
    }

    public void setPlaceId(String placeId) {
        put(KEY_PLACEID, placeId);
    }

    public String getDateVisited() {
        return getString(KEY_DATE_VISIT);
    }

    public void setDateVisited(String dateVisited) {
        put(KEY_DATE_VISIT, dateVisited);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getTimeVisited() {
        return getString(KEY_TIME_VISIT);
    }

    public void setTimeVisited(String timeVisited) {
        put(KEY_TIME_VISIT, timeVisited);
    }

    public String getVisitLength() {
        return getString(KEY_VISIT_LENGTH);
    }

    public void setVisitLength(String length) {
        // stored in 24-hour time
        put(KEY_VISIT_LENGTH, length);
    }
}
