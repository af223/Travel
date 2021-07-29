package com.codepath.travel;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.codepath.travel.models.Event.eventsList;

public class CalendarUtils {

    public static final HashMap<String, Destination> datesOfInterest = new HashMap<>();
    private static final LocalTime curfew = LocalTime.of(23, 1, 0);
    private static final LocalTime breakfastStart = LocalTime.of(8, 0, 0);
    private static final LocalTime breakfastEnd = LocalTime.of(8, 30, 0);
    private static final LocalTime lunchStart = LocalTime.of(12, 0, 0);
    private static final LocalTime lunchEnd = LocalTime.of(13, 0, 0);
    private static final LocalTime dinnerStart = LocalTime.of(18, 0, 0);
    private static final LocalTime dinnerEnd = LocalTime.of(19, 0, 0);
    private static final LocalTime dayStartTime = breakfastEnd.plusMinutes(30);
    private static final HashMap<Destination, Integer> destinationColorCode = new HashMap<>();
    private static final HashMap<String, ArrayList<Pair<LocalTime, LocalTime>>> busyTimeSlots = new HashMap<>();
    private static final HashMap<Destination, LocalDate> nextAvailableDate = new HashMap<>();
    public static LocalDate selectedDate;
    public static ArrayList<LocalDate> days;

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(formatter);
    }

    public static LocalDate getLocalDate(String date) {
        if (date == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalTime getLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String formatStoredTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
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

    public static void getDaysInWeek() {
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
            int red = (rnd.nextInt(256) + 255) / 2;
            int green = ((rnd.nextInt(256) + 255) / 2) % 254 + 1;
            int blue = (rnd.nextInt(256) + 255) / 2;
            int color = Color.argb(255, red, green, blue);
            destinationColorCode.put(destination, color);
        }
    }

    public static int getDestinationColor(Destination destination) {
        return destinationColorCode.get(destination);
    }

    public static void onReloadEverything() {
        destinationColorCode.clear();
        datesOfInterest.clear();
        busyTimeSlots.clear();
        nextAvailableDate.clear();
    }

    public static void setUpTimeSlots(ArrayList<TouristDestination> scheduledEvents) {
        for (TouristDestination touristDestination : scheduledEvents) {
            String dateOfVisit = touristDestination.getDateVisited();
            if (!busyTimeSlots.containsKey(dateOfVisit)) {
                busyTimeSlots.put(dateOfVisit, new ArrayList<>());
            }
            LocalTime timeOfVisit = getLocalTime(touristDestination.getTimeVisited());
            LocalTime endVisitTime = getLocalTime(touristDestination.getVisitEnd());
            Pair block = new Pair(timeOfVisit, endVisitTime);
            busyTimeSlots.get(dateOfVisit).add(block);
            Event event = new Event(touristDestination.getName(), getLocalDate(touristDestination.getDateVisited()), timeOfVisit, endVisitTime);
            eventsList.add(event);
        }
        Iterator it = busyTimeSlots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Pair<LocalTime, LocalTime>>> date = (Map.Entry) it.next();
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

    public static void scheduleTheseEvents(ArrayList<TouristDestination> unscheduledEvents) {
        for (TouristDestination touristDestination : unscheduledEvents) {
            LocalDate dateOfVisit = getLocalDate(touristDestination.getDestination().getDate()).plusDays(1);
            LocalTime timeOfVisit = startOutsideMeals(dayStartTime);
            LocalTime endOfVisit = timeOfVisit.plusHours(2);
            outer:
            while (true) {
                // no events on this day
                if (!busyTimeSlots.containsKey(dateOfVisit.toString())) {
                    busyTimeSlots.put(dateOfVisit.toString(), new ArrayList<>());
                    if (isDuringMeal(endOfVisit)) {
                        timeOfVisit = startOutsideMeals(timeOfVisit.plusHours(2));
                    }
                    addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, 0);
                    break;
                }
                // before first scheduled event of the day
                ArrayList<Pair<LocalTime, LocalTime>> blockedTimes = busyTimeSlots.get(dateOfVisit.toString());
                if (endOfVisit.isBefore(blockedTimes.get(0).first.plusMinutes(1))
                        && !coincidesWithMeal(timeOfVisit, endOfVisit)) {
                    addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, 0);
                    break;
                }
                // between already busy blocks of time
                timeOfVisit = blockedTimes.get(0).second.plusMinutes(15);
                endOfVisit = timeOfVisit.plusHours(2);
                for (int i = 1; i < blockedTimes.size(); i++) {
                    timeOfVisit = startOutsideMeals(timeOfVisit);
                    if (timeOfVisit.isAfter(blockedTimes.get(i - 1).second)
                            && endOfVisit.isBefore(blockedTimes.get(i).first.plusMinutes(1))
                            && !coincidesWithMeal(timeOfVisit, endOfVisit)) {
                        addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, i);
                        break outer;
                    } else {
                        timeOfVisit = blockedTimes.get(i).second.plusMinutes(15);
                        endOfVisit = timeOfVisit.plusHours(2);
                    }
                }
                // after all busy blocks of the day
                if (timeOfVisit.isAfter(blockedTimes.get(blockedTimes.size() - 1).second)
                        && timeOfVisit.isBefore(curfew.minusHours(2))) {
                    if (!coincidesWithMeal(timeOfVisit, endOfVisit)) {
                        addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, blockedTimes.size());
                    } else {
                        timeOfVisit = startOutsideMeals(endOfVisit);
                        addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, blockedTimes.size());
                    }
                    break;
                }
                timeOfVisit = dayStartTime;
                endOfVisit = timeOfVisit.plusHours(2);
                dateOfVisit = dateOfVisit.plusDays(1);
            }
        }
    }

    private static void addEventToSchedule(TouristDestination touristDestination, LocalDate dateOfVisit, LocalTime timeOfVisit, int index) {
        Event event = new Event(touristDestination.getName(), dateOfVisit, timeOfVisit, timeOfVisit.plusHours(2));
        eventsList.add(event);
        Pair block = new Pair(timeOfVisit, timeOfVisit.plusHours(2));
        busyTimeSlots.get(dateOfVisit.toString()).add(index, block);
        saveEvent(touristDestination, dateOfVisit.toString(), timeOfVisit.toString(), formatStoredTime(event.getEndTime()));
    }

    public static void saveEvent(TouristDestination touristDestination, String dateVisited,
                                 String timeVisited, String visitEnd) {
        touristDestination.setDateVisited(dateVisited);
        touristDestination.setVisitEnd(visitEnd);
        touristDestination.setTimeVisited(timeVisited);
        touristDestination.saveInBackground();
    }

    private static Boolean coincidesWithMeal(LocalTime startTime, LocalTime endTime) {
        return coincidesWithTime(startTime, endTime, breakfastStart, breakfastEnd)
                || coincidesWithTime(startTime, endTime, lunchStart, lunchEnd)
                || coincidesWithTime(startTime, endTime, dinnerStart, dinnerEnd);
    }

    private static Boolean coincidesWithTime(LocalTime startTime, LocalTime endTime, LocalTime busyStart, LocalTime busyEnd) {
        return (endTime.isAfter(busyStart) && endTime.isBefore(busyEnd))
                || (startTime.isAfter(busyStart) && startTime.isBefore(busyEnd))
                || (startTime.isBefore(busyStart) && endTime.isAfter(busyEnd));
    }

    private static LocalTime startOutsideMeals(LocalTime time) {
        if (isDuringBreakfast(time)) {
            return breakfastEnd.plusMinutes(15);
        }
        if (isDuringLunch(time)) {
            return lunchEnd.plusMinutes(15);
        }
        if (isDuringDinner(time)) {
            return dinnerEnd.plusMinutes(15);
        }
        return time;
    }

    private static Boolean isDuringMeal(LocalTime time) {
        if (isDuringBreakfast(time)) {
            return true;
        }
        if (isDuringLunch(time)) {
            return true;
        }
        return isDuringDinner(time);
    }

    private static Boolean isDuringBreakfast(LocalTime time) {
        return time.isAfter(breakfastStart) && time.isBefore(breakfastEnd);
    }

    private static Boolean isDuringLunch(LocalTime time) {
        return time.isAfter(lunchStart) && time.isBefore(lunchEnd);
    }

    private static Boolean isDuringDinner(LocalTime time) {
        return time.isAfter(dinnerStart) && time.isBefore(dinnerEnd);
    }
}
