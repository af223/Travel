package com.codepath.travel.models;

import android.text.BoringLayout;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Expense")
public class Expense extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_NAME = "expenseName";
    public static final String KEY_COST = "expenseCost";
    private Boolean isProtected = false;

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getCost() {
        return getString(KEY_COST);
    }

    public void setCost(String cost) {
        put(KEY_COST, cost);
    }

    public Boolean isProtected() {
        return isProtected;
    }

    public void setIsEditable() {
        isProtected = false;
    }

    public void setIsProtected() {
        isProtected = true;
    }
}
