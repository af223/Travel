package com.codepath.travel;

import android.graphics.Color;

import com.codepath.travel.models.Destination;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

public class CalendarUtils {

    public static LocalDate selectedDate;
    public static ArrayList<LocalDate> days;
    public static Destination selectedDestination;
    public static HashMap<Destination, Integer> destinationColorCode = new HashMap<>();
    public static HashMap<String, Destination> datesOfInterest = new HashMap<>();

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
}
