package com.codepath.travel.fragments;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.travel.CalendarUtils;
import com.codepath.travel.R;
import com.codepath.travel.adapters.FlightsAdapter;
import com.codepath.travel.adapters.RoundtripsAdapter;
import com.codepath.travel.models.Flight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Headers;

public class Ticket {

    public static final String[] sortMethods = {"", "Cost", "Departure Date", "Airline"};
    public static final Comparator<Flight> compareCost = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            if (Integer.parseInt(o1.getFlightCost()) < Integer.parseInt(o2.getFlightCost())) {
                return -1;
            } else if (Integer.parseInt(o1.getFlightCost()) > Integer.parseInt(o2.getFlightCost())) {
                return 1;
            } else {
                return 0;
            }
        }
    };
    public static final Comparator<Flight> compareDate = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            LocalDate date1 = CalendarUtils.getLocalDate(o1.getDate());
            LocalDate date2 = CalendarUtils.getLocalDate(o2.getDate());
            if (date1.isBefore(date2)) {
                return -1;
            } else if (date1.isAfter(date2)) {
                return 1;
            } else {
                return 0;
            }
        }
    };
    public static final Comparator<Flight> compareAirline = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            return o1.getCarrier().compareTo(o2.getCarrier());
        }
    };
    private static final String getRoutesURLBase = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/%1$s/%2$s/anytime?inboundpartialdate=anytime";
    private static final String getRoundtripURLBase = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en/%1$s/%2$s/anytime/anytime";
    private static final String rapidapiHostURL = "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com";
    public static Flight chosenOutboundFlight;
    public static Flight chosenInboundFlight;
    private final Dictionary<Integer, String> placesCode;
    private final Dictionary<Integer, String> placesName;
    private final Dictionary<Integer, String> carriers;
    private final String TAG;
    private final Activity activity;
    private final ProgressBar pbFlights;

    public Ticket(String TAG, Activity activity, ProgressBar pbFlights) {
        this.placesCode = new Hashtable<>();
        this.placesName = new Hashtable<>();
        this.carriers = new Hashtable<>();
        this.TAG = TAG;
        this.activity = activity;
        this.pbFlights = pbFlights;
    }

    public static void displayTicket(Flight flight, View ticket) {
        TextView tvDepartAirport = ticket.findViewById(R.id.tvDepartAirport);
        TextView tvArriveAirport = ticket.findViewById(R.id.tvArriveAirport);
        TextView tvCost = ticket.findViewById(R.id.tvCost);
        TextView tvAirline = ticket.findViewById(R.id.tvAirline);
        TextView tvDate = ticket.findViewById(R.id.tvDate);

        tvDepartAirport.setText(flight.getDepartAirportName());
        tvArriveAirport.setText(flight.getArriveAirportName());
        String formattedCost = "$" + flight.getFlightCost();
        tvCost.setText(formattedCost);
        tvAirline.setText(flight.getCarrier());
        tvDate.setText(flight.getDate());
    }

    public void getFlights(String originCode, String destinationCode,
                           FlightsAdapter adapter, ArrayList<Flight> flights) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", activity.getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", rapidapiHostURL);
        client.get(String.format(getRoutesURLBase, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            processPlaces(json.jsonObject.getJSONArray("Places"));
                            processCarriers(json.jsonObject.getJSONArray("Carriers"));
                            processFlights(json.jsonObject.getJSONArray("Quotes"), flights);
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
                        Toast.makeText(activity, "Unable to get flights", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getRoundtripFlights(String originCode, String destinationCode,
                                    RoundtripsAdapter adapter, ArrayList<Pair<Flight, Flight>> flights) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", activity.getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", rapidapiHostURL);
        client.get(String.format(getRoundtripURLBase, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            processPlaces(json.jsonObject.getJSONArray("Places"));
                            processCarriers(json.jsonObject.getJSONArray("Carriers"));
                            processRoundtripFlights(json.jsonObject.getJSONArray("Quotes"), flights);
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
                        Toast.makeText(activity, "Unable to get flights", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processPlaces(JSONArray jsonArray) {
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

    private void processCarriers(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                carriers.put(jsonArray.getJSONObject(i).getInt("CarrierId"), jsonArray.getJSONObject(i).getString("Name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to process carriers", e);
            e.printStackTrace();
        }
    }

    private void processFlights(JSONArray jsonArray, ArrayList<Flight> flights) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);
                String cost = String.valueOf(flightObject.getInt("MinPrice"));
                Flight flight = proccessOneLegOfFlight(flightObject, "OutboundLeg", cost, false);
                flights.add(flight);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to processFlights", e);
            e.printStackTrace();
        }
    }

    private void processRoundtripFlights(JSONArray jsonArray, ArrayList<Pair<Flight, Flight>> flights) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);
                String cost = String.valueOf(flightObject.getInt("MinPrice"));
                Flight outbound = proccessOneLegOfFlight(flightObject, "OutboundLeg", cost, true);
                Flight inbound = proccessOneLegOfFlight(flightObject, "InboundLeg", cost, true);
                Pair flightsPair = new Pair(outbound, inbound);
                flights.add(flightsPair);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Unable to processFlights", e);
            e.printStackTrace();
        }
    }

    private Flight proccessOneLegOfFlight(JSONObject flightObject, String whichLeg, String cost, Boolean isRoundtrip) throws JSONException {
        JSONArray carrierIds = flightObject.getJSONObject(whichLeg).getJSONArray("CarrierIds");
        String carrier = carriers.get(carrierIds.get(0));
        Integer originId = flightObject.getJSONObject(whichLeg).getInt("OriginId");
        String departAirportName = placesName.get(originId);
        String departAirportCode = placesCode.get(originId);
        Integer destinationId = flightObject.getJSONObject(whichLeg).getInt("DestinationId");
        String arriveAirportName = placesName.get(destinationId);
        String arriveAirportCode = placesCode.get(destinationId);
        String date = flightObject.getJSONObject(whichLeg).getString("DepartureDate").substring(0, 10);
        Flight flight = new Flight(departAirportCode, departAirportName, arriveAirportCode,
                arriveAirportName, cost, carrier, date, isRoundtrip);
        return flight;
    }

    public static void onSortTickets(FlightsAdapter adapter, RecyclerView rvFlights, ArrayList<Flight> flights, int position) {
        switch (sortMethods[position]) {
            case "Cost":
                Collections.sort(flights, compareCost);
                break;
            case "Departure Date":
                Collections.sort(flights, compareDate);
                break;
            case "Airline":
                Collections.sort(flights, compareAirline);
                break;
        }
        adapter.notifyDataSetChanged();
        rvFlights.smoothScrollToPosition(0);
    }
}
