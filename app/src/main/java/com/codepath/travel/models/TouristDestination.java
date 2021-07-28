package com.codepath.travel.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TouristDestination")
public class TouristDestination extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_DESTINATION = "destination";
    public static final String KEY_PLACEID = "placeId";
    public static final String KEY_DATE_VISIT = "dateVisited";
    public static final String KEY_NAME = "businessName";
    public static final String KEY_TIME_VISIT = "timeVisited";
    public static final String KEY_VISIT_END = "timeLeft";
    public static final String KEY_IMAGE = "imageURL";
    public static final String KEY_YELP = "yelpURL";
    public static final String KEY_RATING = "rating";
    public static final String KEY_COMMENT = "commentCount";

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

    public String getVisitEnd() {
        return getString(KEY_VISIT_END);
    }

    public void setVisitEnd(String endTime) {
        // stored in 24-hour time
        put(KEY_VISIT_END, endTime);
    }

    public String getImageURL() {
        return getString(KEY_IMAGE);
    }

    public void setImageURL(String imageURL) {
        put(KEY_IMAGE, imageURL);
    }

    public String getYelpURL() {
        return getString(KEY_YELP);
    }

    public void setYelpURL(String yelpURL) {
        put(KEY_YELP, yelpURL);
    }

    public String getRating() {
        return getString(KEY_RATING);
    }

    public void setRating(String rating) {
        put(KEY_RATING, rating);
    }

    public String getCommentCount() {
        return getString(KEY_COMMENT);
    }

    public void setCommentCount(Integer commentCount) {
        put(KEY_COMMENT, String.valueOf(commentCount));
    }
}
