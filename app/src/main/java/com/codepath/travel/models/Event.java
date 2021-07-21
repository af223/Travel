package com.codepath.travel.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Event {

    public static ArrayList<Event> eventsList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : eventsList) {
            if (event.getDate().equals(date)) {
                events.add(event);
            }
        }
        events.sort(new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                if (event1.getTime().isBefore(event2.getTime())) {
                    return -1;
                } else if (event1.getTime().isAfter(event2.getTime())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return events;
    }

    private String name;
    private LocalDate date;
    private LocalTime time;

    public Event(String name, LocalDate date, LocalTime time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
