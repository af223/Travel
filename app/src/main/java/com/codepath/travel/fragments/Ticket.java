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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import okhttp3.Headers;

public class Ticket {

    public static final String[] SORT_METHODS = {"", "Cost", "Departure Date", "Airline"};
    public static final Comparator<Flight> COMPARE_COST = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            return Integer.parseInt(o1.getFlightCost()) - Integer.parseInt(o2.getFlightCost());
        }
    };
    public static final Comparator<Flight> COMPARE_DATE = new Comparator<Flight>() {
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
    public static final Comparator<Flight> COMPARE_AIRLINE = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            return o1.getCarrier().compareTo(o2.getCarrier());
        }
    };
    private static final String GET_ROUTES_URL_BASE = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en-US/%1$s/%2$s/anytime?inboundpartialdate=anytime";
    private static final String GET_ROUNDTRIP_URL_BASE = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/US/USD/en/%1$s/%2$s/anytime/anytime";
    private static final String RAPIDAPI_HOST_URL = "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com";
    public static Flight chosenOutboundFlight;
    public static Flight chosenInboundFlight;
    private final HashMap<Integer, String> placesCode;
    private final HashMap<Integer, String> placesName;
    private final HashMap<Integer, String> carriers;
    private final String TAG;
    private final Activity activity;
    private final ProgressBar pbFlights;
    private final Gson gson;

    public Ticket(String TAG, Activity activity, ProgressBar pbFlights) {
        this.placesCode = new HashMap<>();
        this.placesName = new HashMap<>();
        this.carriers = new HashMap<>();
        this.TAG = TAG;
        this.activity = activity;
        this.pbFlights = pbFlights;
        GsonBuilder builder = new GsonBuilder();
        this.gson = builder.create();
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

    public static void onSortTickets(FlightsAdapter adapter, RecyclerView rvFlights, ArrayList<Flight> flights, int position) {
        switch (SORT_METHODS[position]) {
            case "Cost":
                Collections.sort(flights, COMPARE_COST);
                break;
            case "Departure Date":
                Collections.sort(flights, COMPARE_DATE);
                break;
            case "Airline":
                Collections.sort(flights, COMPARE_AIRLINE);
                break;
        }
        adapter.notifyDataSetChanged();
        rvFlights.smoothScrollToPosition(0);
    }

    public void getFlights(String originCode, String destinationCode,
                           FlightsAdapter adapter, ArrayList<Flight> flights) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        RequestParams params = new RequestParams();
        headers.put("x-rapidapi-key", activity.getResources().getString(R.string.rapid_api_key));
        headers.put("x-rapidapi-host", RAPIDAPI_HOST_URL);
        client.get(String.format(GET_ROUTES_URL_BASE, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        FlightRoutes flightRoutes = gson.fromJson(String.valueOf(json.jsonObject), FlightRoutes.class);
                        processPlaces(flightRoutes);
                        processCarriers(flightRoutes);
                        processFlights(flightRoutes, flights);
                        pbFlights.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
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
        headers.put("x-rapidapi-host", RAPIDAPI_HOST_URL);
        client.get(String.format(GET_ROUNDTRIP_URL_BASE, originCode, destinationCode),
                headers, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        FlightRoutes flightRoutes = gson.fromJson(String.valueOf(json.jsonObject), FlightRoutes.class);
                        processPlaces(flightRoutes);
                        processCarriers(flightRoutes);
                        processRoundtripFlights(flightRoutes, flights);
                        pbFlights.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Flights request failed: ", throwable);
                        Toast.makeText(activity, "Unable to get flights", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processCarriers(FlightRoutes flightRoutes) {
        ArrayList<CarrierCode> carrierCodes = flightRoutes.getCarriers();
        for (CarrierCode carrier : carrierCodes) {
            carriers.put(carrier.getCarrierId(), carrier.getName());
        }
    }

    private void processPlaces(FlightRoutes flightRoutes) {
        ArrayList<PlacesCode> places = flightRoutes.getPlaces();
        for (PlacesCode place : places) {
            if (place.getIataCode() != null) {
                placesCode.put(place.getPlaceId(), place.getIataCode());
            } else {
                placesCode.put(place.getPlaceId(), place.getSkyscannerCode());
            }
            placesName.put(place.getPlaceId(), place.getName());
        }
    }

    private void processFlights(FlightRoutes flightRoutes, ArrayList<Flight> flights) {
        ArrayList<Quote> quotes = flightRoutes.getQuotes();
        for (Quote quote : quotes) {
            String cost = String.valueOf(quote.getMinPrice());
            flights.add(proccessLegOfFlight(quote.getOutboundLeg(), cost, false));
        }
    }

    private void processRoundtripFlights(FlightRoutes flightRoutes, ArrayList<Pair<Flight, Flight>> flights) {
        ArrayList<Quote> quotes = flightRoutes.getQuotes();
        for (Quote quote : quotes) {
            String cost = String.valueOf(quote.getMinPrice());
            Flight outbound = proccessLegOfFlight(quote.getOutboundLeg(), cost, true);
            Flight inbound = proccessLegOfFlight(quote.getInboundLeg(), cost, true);
            Pair flightsPair = new Pair(outbound, inbound);
            flights.add(flightsPair);
        }
    }

    private Flight proccessLegOfFlight(FlightLeg flightLeg, String cost, Boolean isRoundtrip) {
        String carrier = carriers.get(flightLeg.getCarrierIds().get(0));
        Integer originId = flightLeg.getOriginId();
        String departAirportName = placesName.get(originId);
        String departAirportCode = placesCode.get(originId);
        Integer destinationId = flightLeg.getDestinationId();
        String arriveAirportName = placesName.get(destinationId);
        String arriveAirportCode = placesCode.get(destinationId);
        String date = flightLeg.getDepartureDate().substring(0, 10);
        Flight flight = new Flight(departAirportCode, departAirportName, arriveAirportCode,
                arriveAirportName, cost, carrier, date, isRoundtrip);
        return flight;
    }

    class FlightRoutes {
        private final ArrayList<Quote> Quotes = new ArrayList<Quote>();
        private final ArrayList<CarrierCode> Carriers = new ArrayList<CarrierCode>();
        private final ArrayList<PlacesCode> Places = new ArrayList<PlacesCode>();

        public ArrayList<Quote> getQuotes() {
            return Quotes;
        }

        public ArrayList<CarrierCode> getCarriers() {
            return Carriers;
        }

        public ArrayList<PlacesCode> getPlaces() {
            return Places;
        }
    }

    class Quote {
        private Integer MinPrice;
        private FlightLeg OutboundLeg;
        private FlightLeg InboundLeg;

        public Integer getMinPrice() {
            return MinPrice;
        }

        public FlightLeg getOutboundLeg() {
            return OutboundLeg;
        }

        public FlightLeg getInboundLeg() {
            return InboundLeg;
        }
    }

    class FlightLeg {
        private Integer OriginId;
        private Integer DestinationId;
        private final ArrayList<Integer> CarrierIds = new ArrayList<Integer>();
        private String DepartureDate;

        public Integer getOriginId() {
            return OriginId;
        }

        public Integer getDestinationId() {
            return DestinationId;
        }

        public ArrayList<Integer> getCarrierIds() {
            return CarrierIds;
        }

        public String getDepartureDate() {
            return DepartureDate;
        }
    }

    class CarrierCode {
        private Integer CarrierId;
        private String Name;

        public Integer getCarrierId() {
            return CarrierId;
        }

        public String getName() {
            return Name;
        }
    }

    class PlacesCode {
        private String Name;
        private Integer PlaceId;
        private String IataCode;
        private String SkyscannerCode;

        public String getName() {
            return Name;
        }

        public Integer getPlaceId() {
            return PlaceId;
        }

        public String getIataCode() {
            return IataCode;
        }

        public String getSkyscannerCode() {
            return SkyscannerCode;
        }
    }
}
