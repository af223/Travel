package com.codepath.travel;

import android.content.Intent;
import android.graphics.Color;

import androidx.core.util.Pair;

import com.codepath.travel.models.Destination;
import com.codepath.travel.models.Event;
import com.codepath.travel.models.TouristDestination;

import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.codepath.travel.models.Event.eventsList;

public class CalendarUtils {

    public static LocalDate selectedDate;
    public static ArrayList<LocalDate> days;
    public static Destination selectedDestination;
    public static HashMap<Destination, Integer> destinationColorCode = new HashMap<>();
    public static HashMap<String, Destination> datesOfInterest = new HashMap<>();
    public static HashMap<String, ArrayList<Pair<LocalTime, LocalTime>>> busyTimeSlots = new HashMap<>();
    public static HashMap<Destination, LocalDate> nextAvailableDate = new HashMap<>();
    private static LocalTime curfew = LocalTime.of(23, 1, 0);

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
        Iterator it = busyTimeSlots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Pair<LocalTime, LocalTime>>> date = (Map.Entry)it.next();
            date.getValue().sort(new Comparator<Pair<LocalTime, LocalTime>>() {
                @Override
                public int compare(Pair<LocalTime, LocalTime> time1, Pair<LocalTime, LocalTime> time2) {
                    if (time1.first.isBefore(time2.first)) {
                        return -1;
                    } else if (time1.first.isAfter(time2.first)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }

    public static void scheduleUnscheduledEvents(ArrayList<TouristDestination> unscheduledEvents) {
        for (TouristDestination touristDestination : unscheduledEvents) {
            LocalDate dateOfVisit = getLocalDate(touristDestination.getDestination().getDate()).plusDays(1);
            LocalTime timeOfVisit = LocalTime.of(8, 0, 0);;
            outer: while (true) {
                if (!busyTimeSlots.containsKey(dateOfVisit.toString())) {
                    busyTimeSlots.put(dateOfVisit.toString(), new ArrayList<>());
                    addEventToSchedule(touristDestination.getName(), dateOfVisit, timeOfVisit, 0);
                    break;
                }
                ArrayList<Pair<LocalTime, LocalTime>> blockedTimes = busyTimeSlots.get(dateOfVisit.toString());
                if (timeOfVisit.plusHours(2).isBefore(blockedTimes.get(0).first.plusMinutes(1))) {
                    addEventToSchedule(touristDestination.getName(), dateOfVisit, timeOfVisit, 0);
                    break;
                }
                timeOfVisit = blockedTimes.get(0).second.plusMinutes(15);
                for(int i = 1; i < blockedTimes.size(); i++) {
                    if (timeOfVisit.isAfter(blockedTimes.get(i-1).second) && timeOfVisit.plusHours(2).isBefore(blockedTimes.get(i).first.plusMinutes(1))) {
                        addEventToSchedule(touristDestination.getName(), dateOfVisit, timeOfVisit, i);
                        break outer;
                    } else {
                        timeOfVisit = blockedTimes.get(i).second.plusMinutes(15);
                    }
                }
                if (timeOfVisit.isAfter(blockedTimes.get(blockedTimes.size()-1).second)
                        && timeOfVisit.plusHours(2).isBefore(curfew)) {
                    addEventToSchedule(touristDestination.getName(), dateOfVisit, timeOfVisit, blockedTimes.size());
                    break;
                }
                timeOfVisit = LocalTime.of(8, 0, 0);
                dateOfVisit = dateOfVisit.plusDays(1);
            }
        }
    }

    private static void addEventToSchedule(String name, LocalDate dateOfVisit, LocalTime timeOfVisit, int index) {
        Event event = new Event(name, dateOfVisit, timeOfVisit);
        eventsList.add(event);
        Pair block = new Pair(timeOfVisit, timeOfVisit.plusHours(2));
        busyTimeSlots.get(dateOfVisit.toString()).add(index, block);
    }
}
