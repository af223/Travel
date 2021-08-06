package com.codepath.travel;

import com.codepath.travel.models.TouristDestination;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalendarUnitTest {
    @Test
    public void getDaysInMonth_isCorrect() {
        CalendarUtils.days = new ArrayList<>();
        LocalDate date1 = LocalDate.of(2021, 8, 5);
        LocalDate date2 = LocalDate.of(2021, 8, 31);
        LocalDate date3 = LocalDate.of(2021, 8, 1);
        CalendarUtils.selectedDate = date1;
        CalendarUtils.getDaysInMonth();
        assertEquals(LocalDate.of(2021, 8, 1), CalendarUtils.days.get(0));
        assertEquals(LocalDate.of(2021, 8, 31), CalendarUtils.days.get(30));
        assertNull(CalendarUtils.days.get(31));
        assertEquals(42, CalendarUtils.days.size());
        CalendarUtils.selectedDate = date2;
        CalendarUtils.getDaysInMonth();
        assertEquals(LocalDate.of(2021, 8, 1), CalendarUtils.days.get(0));
        CalendarUtils.selectedDate = date3;
        CalendarUtils.getDaysInMonth();
        assertEquals(LocalDate.of(2021, 8, 1), CalendarUtils.days.get(0));

        LocalDate firstDay = LocalDate.of(2021, 1, 1);
        CalendarUtils.selectedDate = firstDay;
        CalendarUtils.getDaysInMonth();
        assertEquals(LocalDate.of(2021, 1, 1), CalendarUtils.days.get(5));

        LocalDate lastDay = LocalDate.of(2021, 12, 31);
        CalendarUtils.selectedDate = lastDay;
        CalendarUtils.getDaysInMonth();
        assertEquals(LocalDate.of(2021, 12, 1), CalendarUtils.days.get(3));
        assertEquals(LocalDate.of(2021, 12, 31), CalendarUtils.days.get(33));
        assertNull(CalendarUtils.days.get(34));
        assertNull(CalendarUtils.days.get(40));
    }

    @Test
    public void sundayForDay_isCorrect() {
        LocalDate date = LocalDate.of(2021, 8, 5);
        LocalDate result = CalendarUtils.sundayForDate(date);
        assertEquals(LocalDate.of(2021, 8, 1), result);

        date = LocalDate.of(2021, 7, 2);
        result = CalendarUtils.sundayForDate(date);
        assertEquals(LocalDate.of(2021, 6, 27), result);

        date = LocalDate.of(2021, 6, 30);
        result = CalendarUtils.sundayForDate(date);
        assertEquals(LocalDate.of(2021, 6, 27), result);
    }

    @Test
    public void getDaysInWeek_isCorrect() {
        CalendarUtils.days = new ArrayList<>();

        CalendarUtils.selectedDate = LocalDate.of(2021, 8, 6);
        CalendarUtils.getDaysInWeek();
        assertEquals(LocalDate.of(2021, 8,1), CalendarUtils.days.get(0));

        CalendarUtils.selectedDate = LocalDate.of(2021, 7, 1);
        CalendarUtils.getDaysInWeek();
        assertEquals(LocalDate.of(2021, 6, 27), CalendarUtils.days.get(0));

        CalendarUtils.selectedDate = LocalDate.of(2021, 6, 30);
        CalendarUtils.getDaysInWeek();
        assertEquals(LocalDate.of(2021, 6, 27), CalendarUtils.days.get(0));

        CalendarUtils.selectedDate = LocalDate.of(2022, 1, 1);
        CalendarUtils.getDaysInWeek();
        assertEquals(LocalDate.of(2021, 12, 26), CalendarUtils.days.get(0));
    }
}