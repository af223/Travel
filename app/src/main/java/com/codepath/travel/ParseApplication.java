package com.codepath.travel;

import android.app.Application;

import com.codepath.travel.models.Destination;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Destination.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.app_id))
                .clientKey(getString(R.string.client_key))
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
