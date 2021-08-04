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

    public static final HashMap<String, Destination> DATES_OF_INTEREST = new HashMap<>();
    private static final LocalTime CURFEW = LocalTime.of(23, 1, 0);
    private static final LocalTime BREAKFAST_START = LocalTime.of(8, 0, 0);
    private static final LocalTime BREAKFAST_END = LocalTime.of(8, 30, 0);
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0, 0);
    private static final LocalTime DINNER_START = LocalTime.of(18, 0, 0);
    private static final LocalTime DINNER_END = LocalTime.of(19, 0, 0);
    private static final LocalTime DAY_START_TIME = BREAKFAST_END.plusMinutes(30);
    private static final HashMap<Destination, Integer> DESTINATION_COLOR_CODE = new HashMap<>();
    private static final HashMap<String, ArrayList<Pair<LocalTime, LocalTime>>> BUSY_TIME_SLOTS = new HashMap<>();
    private static final HashMap<String, Pair<LocalDate, Integer>> NEXT_AVAILABLE_DATE = new HashMap<>();
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

    /**
     * Populates the days array with the days of the month containing the selected date. "days" array
     * contains exactly 42 elements (6 weeks/rows x 7 days a week/row) for month view. The ith element in "days" represents
     * the ith calendar cell. If the ith calendar cell of that month should be empty (i.e. no date associated), then
     * the ith element of "days" is null, otherwise, ith element is the LocalDate.
     */
    public static void getDaysInMonth() {
        days.clear();
        YearMonth yearMonth = YearMonth.from(selectedDate);
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

    /**
     * Populates the days array with the days of the week containing the selected date. "days" array
     * contains exactly 7 elements (7 days for selected week) for week view. The ith element in "days"
     * represents the ith day of the week, starting with Sunday at index 0.
     */
    public static void getDaysInWeek() {
        days.clear();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);
        while (current.isBefore(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }
    }

    /**
     * @param current - the selected LocalDate
     * @return current if current is a Sunday, otherwise it returns the LocalDate that is the closest
     * Sunday prior to current
     */
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
            float[] hsv = new float[3];
            hsv[0] = rnd.nextInt(360); // hue
            hsv[1] = 80; // saturation
            hsv[2] = 100; // value
            int color = Color.HSVToColor(60, hsv);
            DESTINATION_COLOR_CODE.put(destination, color);
        }
    }

    public static int getDestinationColor(Destination destination) {
        return DESTINATION_COLOR_CODE.get(destination);
    }

    public static void onReloadEverything() {
        DESTINATION_COLOR_CODE.clear();
        DATES_OF_INTEREST.clear();
        BUSY_TIME_SLOTS.clear();
        NEXT_AVAILABLE_DATE.clear();
    }

    public static void setupBusyTimes(ArrayList<TouristDestination> scheduledEvents) {
        for (TouristDestination touristDestination : scheduledEvents) {
            String dateOfVisit = touristDestination.getDateVisited();
            if (!BUSY_TIME_SLOTS.containsKey(dateOfVisit)) {
                BUSY_TIME_SLOTS.put(dateOfVisit, new ArrayList<>());
            }
            LocalTime timeOfVisit = getLocalTime(touristDestination.getTimeVisited());
            LocalTime endVisitTime = getLocalTime(touristDestination.getVisitEnd());
            int insertIndex = BUSY_TIME_SLOTS.get(dateOfVisit).size();
            addEventToSchedule(touristDestination, getLocalDate(dateOfVisit), timeOfVisit, endVisitTime, insertIndex);
        }
        Iterator it = BUSY_TIME_SLOTS.entrySet().iterator();
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
            LocalTime timeOfVisit = startOutsideMeals(DAY_START_TIME);
            LocalTime endOfVisit = timeOfVisit.plusHours(2);
            outer:
            while (true) {
                // no events on this day
                if (!BUSY_TIME_SLOTS.containsKey(dateOfVisit.toString())) {
                    BUSY_TIME_SLOTS.put(dateOfVisit.toString(), new ArrayList<>());
                    if (coincidesWithMeal(timeOfVisit, endOfVisit)) {
                        timeOfVisit = startOutsideMeals(endOfVisit);
                        endOfVisit = timeOfVisit.plusHours(2);
                    }
                    addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, endOfVisit, 0);
                    saveEvent(touristDestination, dateOfVisit.toString(), timeOfVisit.toString(), formatStoredTime(endOfVisit));
                    break;
                }
                // before first scheduled event of the day
                ArrayList<Pair<LocalTime, LocalTime>> blockedTimes = BUSY_TIME_SLOTS.get(dateOfVisit.toString());
                if (endOfVisit.isBefore(blockedTimes.get(0).first.plusMinutes(1))
                        && !coincidesWithMeal(timeOfVisit, endOfVisit)) {
                    addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, endOfVisit, 0);
                    saveEvent(touristDestination, dateOfVisit.toString(), timeOfVisit.toString(), formatStoredTime(endOfVisit));
                    break;
                }
                // between already busy blocks of time
                timeOfVisit = blockedTimes.get(0).second.plusMinutes(15);
                endOfVisit = timeOfVisit.plusHours(2);
                for (int i = 1; i < blockedTimes.size(); i++) {
                    timeOfVisit = startOutsideMeals(timeOfVisit);
                    endOfVisit = timeOfVisit.plusHours(2);
                    if (timeOfVisit.isAfter(blockedTimes.get(i - 1).second)
                            && endOfVisit.isBefore(blockedTimes.get(i).first.plusMinutes(1))
                            && !coincidesWithMeal(timeOfVisit, endOfVisit)) {
                        addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, endOfVisit, i);
                        saveEvent(touristDestination, dateOfVisit.toString(), timeOfVisit.toString(), formatStoredTime(endOfVisit));
                        break outer;
                    } else {
                        timeOfVisit = blockedTimes.get(i).second.plusMinutes(15);
                        endOfVisit = timeOfVisit.plusHours(2);
                    }
                }
                // after all busy blocks of the day
                if (timeOfVisit.isAfter(blockedTimes.get(blockedTimes.size() - 1).second)
                        && timeOfVisit.isBefore(CURFEW.minusHours(2))) {
                    if (coincidesWithMeal(timeOfVisit, endOfVisit)) {
                        timeOfVisit = startOutsideMeals(endOfVisit);
                        endOfVisit = timeOfVisit.plusHours(2);
                    }
                    addEventToSchedule(touristDestination, dateOfVisit, timeOfVisit, endOfVisit, blockedTimes.size());
                    saveEvent(touristDestination, dateOfVisit.toString(), timeOfVisit.toString(), formatStoredTime(endOfVisit));
                    break;
                }
                timeOfVisit = DAY_START_TIME;
                endOfVisit = timeOfVisit.plusHours(2);
                dateOfVisit = dateOfVisit.plusDays(1);
            }
        }
    }

    public static void fillScheduledMeals(ArrayList<TouristDestination> allRestaurants) {
        for (TouristDestination restaurant : allRestaurants) {
            String destinationID = restaurant.getDestination().getObjectId();
            LocalTime timeVisited = getLocalTime(restaurant.getTimeVisited());
            LocalTime endVisitTime = getLocalTime(restaurant.getVisitEnd());
            LocalDate dateVisited = getLocalDate(restaurant.getDateVisited());
            LocalDate nextDate = dateVisited;
            int mealOfDay = 1;
            if (timeVisited.equals(LUNCH_START)) {
                mealOfDay = 2;
            } else if (timeVisited.equals(DINNER_START)) {
                mealOfDay = 0;
                nextDate = nextDate.plusDays(1);
            }
            if (NEXT_AVAILABLE_DATE.get(destinationID) == null) {
                Pair nextMeal = new Pair(nextDate, mealOfDay);
                NEXT_AVAILABLE_DATE.put(destinationID, nextMeal);
            } else {
                LocalDate latestDateVisit = NEXT_AVAILABLE_DATE.get(destinationID).first;
                int latestMeal = NEXT_AVAILABLE_DATE.get(destinationID).second;
                if (latestDateVisit.isBefore(nextDate)) {
                    latestDateVisit = nextDate;
                    latestMeal = mealOfDay;
                } else if (latestDateVisit.equals(nextDate)) {
                    latestMeal = Integer.max(latestMeal, mealOfDay);
                }
                Pair nextMeal = new Pair(latestDateVisit, latestMeal);
                NEXT_AVAILABLE_DATE.put(destinationID, nextMeal);
            }
            addEventToSchedule(restaurant, dateVisited, timeVisited, endVisitTime, -1);
        }
    }

    public static void scheduleMeals(ArrayList<TouristDestination> allRestaurants) {
        for (TouristDestination restaurant : allRestaurants) {
            String destinationID = restaurant.getDestination().getObjectId();
            if (NEXT_AVAILABLE_DATE.get(destinationID) == null) {
                Pair nextMeal = new Pair(getLocalDate(restaurant.getDestination().getDate()).plusDays(1), 0);
                NEXT_AVAILABLE_DATE.put(destinationID, nextMeal);
            }
            LocalDate nextMealDate = NEXT_AVAILABLE_DATE.get(destinationID).first;
            int numMealsPlanned = NEXT_AVAILABLE_DATE.get(destinationID).second;
            switch (numMealsPlanned) {
                case 0:
                    addEventToSchedule(restaurant, nextMealDate, BREAKFAST_START, BREAKFAST_END, -1);
                    saveEvent(restaurant, nextMealDate.toString(), formatStoredTime(BREAKFAST_START), formatStoredTime(BREAKFAST_END));
                    NEXT_AVAILABLE_DATE.put(destinationID, new Pair(nextMealDate, 1));
                    break;
                case 1:
                    addEventToSchedule(restaurant, nextMealDate, LUNCH_START, LUNCH_END, -1);
                    saveEvent(restaurant, nextMealDate.toString(), formatStoredTime(LUNCH_START), formatStoredTime(LUNCH_END));
                    NEXT_AVAILABLE_DATE.put(destinationID, new Pair(nextMealDate, 2));
                    break;
                case 2:
                    addEventToSchedule(restaurant, nextMealDate, DINNER_START, DINNER_END, -1);
                    saveEvent(restaurant, nextMealDate.toString(), formatStoredTime(DINNER_START), formatStoredTime(DINNER_END));
                    NEXT_AVAILABLE_DATE.put(destinationID, new Pair(nextMealDate.plusDays(1), 0));
                    break;
            }
        }
    }

    private static void addEventToSchedule(TouristDestination touristDestination, LocalDate dateOfVisit, LocalTime timeOfVisit, LocalTime endOfVisit, int index) {
        String eventName = touristDestination.getName();
        if (index > -1) {
            Pair block = new Pair(timeOfVisit, endOfVisit);
            BUSY_TIME_SLOTS.get(dateOfVisit.toString()).add(index, block);
        } else {
            eventName = restaurantEventName(timeOfVisit, eventName);
        }
        Event event;
        if (!touristDestination.getPlaceId().equals("N/A")) {
            event = new Event(eventName, dateOfVisit, timeOfVisit, endOfVisit, false);
        } else {
            event = new Event(eventName, dateOfVisit, timeOfVisit, endOfVisit, true);
            event.setTouristDestinationId(touristDestination.getObjectId());
        }
        eventsList.add(event);
    }

    public static void saveEvent(TouristDestination touristDestination, String dateVisited,
                                 String timeVisited, String visitEnd) {
        touristDestination.setDateVisited(dateVisited);
        touristDestination.setVisitEnd(visitEnd);
        touristDestination.setTimeVisited(timeVisited);
        touristDestination.saveInBackground();
    }

    private static String restaurantEventName(LocalTime timeOfVisit, String eventName) {
        if (timeOfVisit.equals(BREAKFAST_START)) {
            return "Breakfast at " + eventName;
        } else if (timeOfVisit.equals(LUNCH_START)) {
            return "Lunch at " + eventName;
        } else {
            return "Dinner at " + eventName;
        }
    }

    private static Boolean coincidesWithMeal(LocalTime startTime, LocalTime endTime) {
        return coincidesWithTime(startTime, endTime, BREAKFAST_START, BREAKFAST_END)
                || coincidesWithTime(startTime, endTime, LUNCH_START, LUNCH_END)
                || coincidesWithTime(startTime, endTime, DINNER_START, DINNER_END);
    }

    private static Boolean coincidesWithTime(LocalTime startTime, LocalTime endTime, LocalTime busyStart, LocalTime busyEnd) {
        return (endTime.isAfter(busyStart) && endTime.isBefore(busyEnd))
                || (startTime.isAfter(busyStart) && startTime.isBefore(busyEnd))
                || (startTime.isBefore(busyStart) && endTime.isAfter(busyEnd));
    }

    private static LocalTime startOutsideMeals(LocalTime time) {
        if (isDuringBreakfast(time)) {
            return BREAKFAST_END.plusMinutes(15);
        }
        if (isDuringLunch(time)) {
            return LUNCH_END.plusMinutes(15);
        }
        if (isDuringDinner(time)) {
            return DINNER_END.plusMinutes(15);
        }
        return time;
    }

    private static Boolean isDuringBreakfast(LocalTime time) {
        return time.isAfter(BREAKFAST_START) && time.isBefore(BREAKFAST_END);
    }

    private static Boolean isDuringLunch(LocalTime time) {
        return time.isAfter(LUNCH_START) && time.isBefore(LUNCH_END);
    }

    private static Boolean isDuringDinner(LocalTime time) {
        return time.isAfter(DINNER_START) && time.isBefore(DINNER_END);
    }
}
