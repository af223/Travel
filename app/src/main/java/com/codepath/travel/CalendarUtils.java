package com.codepath.travel;

import android.content.Intent;
import android.graphics.Color;

import androidx.core.util.Pair;

import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Event;
import com.codepath.travel.models.TouristDestination;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.codepath.travel.models.Event.eventsList;

public class CalendarUtils {

    public static LocalDate selectedDate;
    public static ArrayList<LocalDate> days;
    public static Destination selectedDestination;
    public static HashMap<Destination, Integer> destinationColorCode = new HashMap<>();
    public static HashMap<String, Destination> datesOfInterest = new HashMap<>();
    public static HashMap<String, ArrayList<Pair<LocalTime, LocalTime>>> busyTimeSlots = new HashMap<>();

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String formatDateForEvent(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }

    public static LocalDate getLocalDate(String date) {
        if (date == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalTime getLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static void getDaysInMonth(LocalDate date) {
        days.clear();
        YearMonth yearMonth = YearMonth.from(date);
        int numDaysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday is leftmost column

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > numDaysInMonth + dayOfWeek) {
                days.add(null);
            } else {
                days.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
    }

    public static void getDaysInWeek(LocalDate date) {
        days.clear();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);
        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
    }

    private static LocalDate sundayForDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)) {
            if (current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return current;
            }
            current = current.minusDays(1);
        }
        return null;
    }

    public static void generateDestinationColorCode(ArrayList<Destination> destinations) {
        Random rnd = new Random();
        for (Destination destination : destinations) {
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            destinationColorCode.put(destination, color);
        }
    }

    public static void setUpTimeSlots(ArrayList<TouristDestination> scheduledEvents) {
        for (TouristDestination touristDestination : scheduledEvents) {
            String dateOfVisit = touristDestination.getDateVisited();
            if (!busyTimeSlots.containsKey(dateOfVisit)) {
                busyTimeSlots.put(dateOfVisit, new ArrayList<>());
            }
            LocalTime timeOfVisit = getLocalTime(touristDestination.getTimeVisited());
            Pair block = new Pair(timeOfVisit, timeOfVisit.plusHours(Integer.parseInt(touristDestination.getVisitLength())));
            busyTimeSlots.get(dateOfVisit).add(block);
            Event event = new Event(touristDestination.getName(), getLocalDate(touristDestination.getDateVisited()), timeOfVisit);
            eventsList.add(event);
        }
    }
}
