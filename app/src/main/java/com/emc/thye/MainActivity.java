package com.emc.thye;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.emc.verticalweekcalendar.VerticalWeekCalendar;
import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.interfaces.OnDateClickListener;

import static com.emc.verticalweekcalendar.model.CalendarDay.DEFAULT;
import static com.emc.verticalweekcalendar.model.CalendarDay.SELECTED;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VerticalWeekCalendar calendarView = new VerticalWeekCalendar.Builder()
                .setView(R.id.verticalCalendar)
                .setDateWatcher(new DateWatcher() {
                    @Override
                    public int getStateForDate(int year, int month, int day) {
                        switch (day){
                            case 10: {
                                return SELECTED;
                            }
                            default:
                                return DEFAULT;
                        }
                    }
                })
                .init(this);

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalenderDayClicked(int year, int month, int day) {
                Toast.makeText(getApplicationContext(), year + " " + month + " " + day, Toast.LENGTH_SHORT).show();
            }
        });

        calendarView.setCustomFont("alex_brush_regular.ttf");
    }
}
