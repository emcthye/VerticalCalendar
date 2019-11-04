package com.emc.verticalweekcalendar.controller;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.model.CalendarDay;

public class CalendarDataSourceFactory extends DataSource.Factory<Long, CalendarDay> {

    private DateWatcher dateWatcher;

    @NonNull
    @Override
    public DataSource<Long, CalendarDay> create() {
        CalendarDataSource calendarDataSource = new CalendarDataSource();
        calendarDataSource.setDateWatcher(dateWatcher);
        return calendarDataSource;
    }

    public void setDateWatcher(DateWatcher dateWatcher) {
        this.dateWatcher = dateWatcher;
    }
}
