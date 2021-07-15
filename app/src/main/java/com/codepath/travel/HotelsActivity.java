package com.codepath.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.ByteString;

public class HotelsActivity extends AppCompatActivity {

    private static final String AMADEUS_ACCESS_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private static final String TAG = "HotelsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        
        //requestAccessToken(); last : 4:13 PM
    }

    private void requestAccessToken() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        RequestHeaders headers = new RequestHeaders();
        headers.put("content-type", "application/x-www-form-urlencoded");
        String body = String.format("grant_type=client_credentials&client_id=%1$s&client_secret=%2$s",
                                        getResources().getString(R.string.amadeus_api_key),
                                        getResources().getString(R.string.amadeus_api_secret));
        Request.Builder requestBuilder = new Request.Builder().url(AMADEUS_ACCESS_URL);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/x-www-form-urlencoded"));
        Request request = requestBuilder.post(requestBody).build();
        JsonHttpResponseHandler callback = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "ERROR: ", throwable);
                Log.e(TAG, response);
            }
        };
        okHttpClient.newCall(request).enqueue(callback);
    }
}