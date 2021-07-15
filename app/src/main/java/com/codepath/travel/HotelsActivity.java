package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

public class HotelsActivity extends AppCompatActivity {

    private static final String AMADEUS_ACCESS_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String TAG = "HotelsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        
        requestAccessToken();
    }

    private void requestAccessToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("content-type", "application/x-www-form-urlencoded");
        //String body = String.format("grant_type=client_credentials&client_id=%1$s&client_secret=%2$s",
        //                                getResources().getString(R.string.amadeus_api_key),
        //                                getResources().getString(R.string.amadeus_api_secret));
        String body = "grant_type=client_credentials";
        Log.i(TAG, body);
        client.post(AMADEUS_ACCESS_URL, headers, params, body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "ERROR: ", throwable);
                Log.e(TAG, response);
            }
        });
    }
}