package com.codepath.travel.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CostsFragment extends Fragment {

    private Button btnFlights;

    public CostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_costs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: set up view for summary of costs of user's trip
        // probably using Recycler View
        super.onViewCreated(view, savedInstanceState);

        btnFlights = view.findViewById(R.id.btnFlights);
        btnFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFlights();
            }
        });
    }

    private void getFlights() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", "544d7e92aemsh577fda15cb7dd8bp17602ejsnc267df4e524c");
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        //client.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/SFO-sky/ORD-sky/anytime?inboundpartialdate=anytime",
        client.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query=Czechia", //Czechia
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("Costs", "worked");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("Costs", "failed");
                    }
                });
        /*client.prepare("GET", "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/SFO-sky/ORD-sky/2019-09-01?inboundpartialdate=2019-12-01")
                .setHeader("x-rapidapi-key", "544d7e92aemsh577fda15cb7dd8bp17602ejsnc267df4e524c")
                .setHeader("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
                .execute()
                .toCompletableFuture()
                .thenAccept(System.out::println)
                .join();

        client.close();*/
    }
}