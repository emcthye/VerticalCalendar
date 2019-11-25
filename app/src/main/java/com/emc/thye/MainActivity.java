package com.emc.thye;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.emc.verticalweekcalendar.VerticalWeekCalendar;
import com.emc.verticalweekcalendar.controller.VerticalWeekAdapter;
import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.interfaces.OnDateClickListener;
import com.emc.verticalweekcalendar.model.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    private GregorianCalendar selected;
    private VerticalWeekCalendar calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        selected = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        calendarView = new VerticalWeekCalendar.Builder()
                .setView(R.id.verticalCalendar)
                .setDateWatcher(new DateWatcher() {
                    @Override
                    public int getStateForDate(int year, int month, int day) {
                        int state = selected.compareTo(new GregorianCalendar(year, month, day)) == 0 ?
                                CalendarDay.SELECTED : CalendarDay.DEFAULT;
                        Log.i("onCalenderDayClicked", "1 " + selected.getTimeInMillis());
                        return selected.compareTo(new GregorianCalendar(year, month, day)) == 0 ?
                                CalendarDay.SELECTED : CalendarDay.DEFAULT;
                    }
                })
                .init(this);

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalenderDayClicked(int year, int month, int day) {
                GregorianCalendar selectedDay = new GregorianCalendar(year, month, day);
                if(selected.compareTo(selectedDay) != 0) {
                    Log.i("onCalenderDayClicked", " 2" + new Date(selected.getTimeInMillis()).toString() + " "
                            + new Date(selectedDay.getTimeInMillis()).toString());
                    selected = selectedDay;
                    calendarView.refresh();
                }
            }
        });

//        calendarView.setCustomFont("alex_brush_regular.ttf");
    }
}
