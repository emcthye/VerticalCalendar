package com.emc.verticalweekcalendar.controller;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.model.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarDataSource extends ItemKeyedDataSource<Long, CalendarDay> {

    private DateWatcher dateWatcher;

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<CalendarDay> callback) {
        callback.onResult(createCalendarDays(params.requestedInitialKey, params.requestedLoadSize, true));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<CalendarDay> callback) {
        callback.onResult(createCalendarDays(params.key, params.requestedLoadSize, true));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<CalendarDay> callback) {
        List<CalendarDay> result = createCalendarDays(params.key, params.requestedLoadSize, false);
        Collections.reverse(result);
        callback.onResult(result);
    }

    @NonNull
    @Override
    public Long getKey(@NonNull CalendarDay item) {
        return item.getDay().getTimeInMillis();
    }

    void setDateWatcher(DateWatcher dateWatcher) {
        this.dateWatcher = dateWatcher;
    }

    private int getDateState(int year, int month, int day) {
        return dateWatcher.getStateForDate(year,month,day);
    }

    private List<CalendarDay> createCalendarDays(long startAt, int loadSize, boolean loadAfter) {
        GregorianCalendar startDay = new GregorianCalendar();

        List<CalendarDay> days = new ArrayList<>();
        for(int i = 1; i <= loadSize; i++) {
            startDay.setTimeInMillis(startAt);
            int daysToAppendOrPrepend = loadAfter ? i : i * -1;
            startDay.add(Calendar.DAY_OF_MONTH, daysToAppendOrPrepend);
            CalendarDay newDay = new CalendarDay(
                    startDay.get(Calendar.YEAR),
                    startDay.get(Calendar.MONTH),
                    startDay.get(Calendar.DAY_OF_MONTH));
            if (dateWatcher != null) newDay.setState(getDateState(newDay.getYear(),newDay.getMonth(),newDay.getDay().get(Calendar.DAY_OF_MONTH)));
            days.add(newDay);
        }
        return days;
    }


}
