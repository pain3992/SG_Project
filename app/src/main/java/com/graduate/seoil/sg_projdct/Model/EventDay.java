package com.graduate.seoil.sg_projdct.Model;

/**
 * Created by baejanghun on 06/05/2019.
 */
public class EventDay {
    int Year;
    int Month;
    int Day;

    public EventDay() {
    }

    public EventDay(int year, int month, int day) {
        Year = year;
        Month = month;
        Day = day;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }
}
