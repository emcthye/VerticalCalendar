package com.emc.verticalweekcalendar.interfaces;

import android.graphics.Typeface;

public interface ResProvider {

    public int getSelectedDayBackground();
    public int getSelectedDayTextColor();
    public int getDayTextColor();
    public int getWeekDayTextColor();
    public int getDayBackground();
    public Typeface getCustomFont();
}
