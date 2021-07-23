package com.codepath.travel.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.R;
import com.codepath.travel.adapters.FlightsAdapter;
import com.codepath.travel.models.Flight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;

import okhttp3.Headers;

public class Ticket {

    private static final String getRoutesURLBase = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/%1$s/%2$s/anytime?inboundpartialdate=anytime";
    public static Button btnConfirm;
    public static Flight chosenOutboundFlight;
    public static Flight chosenInboundFlight;

    // User has selected a ticket, but not clicked confirm button yet
    public static void choose(Flight flight, View view, Boolean outbound, Context context) {
        if (outbound) {
            chosenOutboundFlight = flight;
        } else {
            chosenInboundFlight = flight;
        }
        btnConfirm.setClickable(true);
        btnConfirm.setBackgroundColor(context.getResources().getColor(R.color.pastel_pink));

        TextView tvDepartAirport = view.findViewById(R.id.tvDepartAirport);
        TextView tvArriveAirport = view.findViewById(R.id.tvArriveAirport);
        TextView tvCost = view.findViewById(R.id.tvCost);
        TextView tvAirline = view.findViewById(R.id.tvAirline);
        TextView tvDate = view.findViewById(R.id.tvDate);

        tvDepartAirport.setText(flight.getDepartAirportName());
        tvArriveAirport.setText(flight.getArriveAirportName());
        String formattedCost = "$" + flight.getFlightCost();
        tvCost.setText(formattedCost);
        tvAirline.setText(flight.getCarrier());
        tvDate.setText(flight.getDate());
        view.setVisibility(View.VISIBLE);
    }

    public static void getFlights(String originCode, String destinationCode, Activity activity, ProgressBar pbFlights,
                                  FlightsAdapter adapter, String TAG, Context context,
                                  Dictionary<Integer, String> placesCode, Dictionary<Integer, String> placesName,
                                  Dictionary<Integer, String> carriers, ArrayList<Flight> flights) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", activity.getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        client.get(String.format(getRoutesURLBase, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            processPlaces(json.jsonObject.getJSONArray("Places"), placesCode, placesName, TAG);
                            processCarriers(json.jsonObject.getJSONArray("Carriers"), carriers, TAG);
                            processFlights(json.jsonObject.getJSONArray("Quotes"), carriers, placesCode, placesName, flights, TAG);
                            pbFlights.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e(TAG, "unable to parse response", e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Flights request failed: ", throwable);
                        Toast.makeText(context, "Unable to get flights", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void processPlaces(JSONArray jsonArray, Dictionary<Integer, String> placesCode, Dictionary<Integer, String> placesName, String TAG) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).has("IataCode")) {
                    placesCode.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("IataCode"));
                } else {
                    placesCode.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("SkyscannerCode"));
                }
                placesName.put(jsonArray.getJSONObject(i).getInt("PlaceId"), jsonArray.getJSONObject(i).getString("Name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to process places", e);
            e.printStackTrace();
        }
    }

    private static void processCarriers(JSONArray jsonArray, Dictionary<Integer, String> carriers, String TAG) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                carriers.put(jsonArray.getJSONObject(i).getInt("CarrierId"), jsonArray.getJSONObject(i).getString("Name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to process carriers", e);
            e.printStackTrace();
        }
    }

    private static void processFlights(JSONArray jsonArray, Dictionary<Integer, String> carriers,
                                       Dictionary<Integer, String> placesCode, Dictionary<Integer, String> placesName,
                                       ArrayList<Flight> flights, String TAG) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);
                Boolean isDirect = flightObject.getBoolean("Direct");
                String cost = String.valueOf(flightObject.getInt("MinPrice"));
                JSONArray carrierIds = flightObject.getJSONObject("OutboundLeg").getJSONArray("CarrierIds");
                String carrier = carriers.get(carrierIds.get(0));
                Integer originId = flightObject.getJSONObject("OutboundLeg").getInt("OriginId");
                String departAirportName = placesName.get(originId);
                String departAirportCode = placesCode.get(originId);
                Integer destinationId = flightObject.getJSONObject("OutboundLeg").getInt("DestinationId");
                String arriveAirportName = placesName.get(destinationId);
                String arriveAirportCode = placesCode.get(destinationId);
                String date = flightObject.getJSONObject("OutboundLeg").getString("DepartureDate").substring(0, 10);
                Flight flight = new Flight(departAirportCode, departAirportName, arriveAirportCode,
                        arriveAirportName, cost, carrier, date, isDirect, false);
                flights.add(flight);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to processFlights", e);
            e.printStackTrace();
        }
    }
}
