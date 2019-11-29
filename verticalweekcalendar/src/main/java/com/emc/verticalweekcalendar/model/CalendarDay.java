package com.emc.verticalweekcalendar.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarDay implements Serializable {

    public static final int DEFAULT = 0;
    public static final int SELECTED = 1;
    public static final int TODAY = 2;

    private int state = DEFAULT;

    private int year;
    private int month;
    private final GregorianCalendar day;

    public CalendarDay(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = new GregorianCalendar(year,month,day);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public GregorianCalendar getDay() {
        return day;
    }

    @Override
    public String toString(){
        return "Day: " + day.get(Calendar.DAY_OF_MONTH) + " State: " + state;
    }
}
