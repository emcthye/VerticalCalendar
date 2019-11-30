package com.emc.verticalweekcalendar.controller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emc.verticalweekcalendar.R;
import com.emc.verticalweekcalendar.interfaces.DateClickCallback;
import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.interfaces.OnDateClickListener;
import com.emc.verticalweekcalendar.interfaces.ResProvider;
import com.emc.verticalweekcalendar.model.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static com.emc.verticalweekcalendar.model.CalendarDay.DEFAULT;
import static com.emc.verticalweekcalendar.model.CalendarDay.SELECTED;

public class VerticalWeekAdapter extends RecyclerView.Adapter<VerticalWeekAdapter.DayViewHolder> implements DateClickCallback {

    public List<CalendarDay> days = new ArrayList<>();
    private RecyclerView recyclerView;
    private DateWatcher dateWatcher;
    private OnDateClickListener onDateClickListener;
    private ResProvider resProvider;

    public VerticalWeekAdapter(ResProvider resProvider) {
        this.resProvider = resProvider;
        initCalendar();
    }

    private void initCalendar(){

        Calendar now = Calendar.getInstance();

        List<CalendarDay> createdDays = new ArrayList<>();
        for(int i = 0; i <= 15; i++) {
            Calendar today = new GregorianCalendar(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            today.add(Calendar.DAY_OF_MONTH, i * -1);

            CalendarDay createdDay = new CalendarDay(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));

            createdDays.add(createdDay);
        }
        Collections.reverse(createdDays);
        days.addAll(createdDays);

        for(int i = 1; i <= 15; i++) {
            Calendar today = new GregorianCalendar(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            today.add(Calendar.DAY_OF_MONTH, i);

            CalendarDay createdDay = new CalendarDay(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));
            days.add(createdDay);
        }
    }

    public void addCalendarDays(boolean loadAfter) {

        int insertIdx = loadAfter ? days.size() - 1: 0;
        CalendarDay insertionPoint = days.get(insertIdx);

        List<CalendarDay> createdDays = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            Calendar startDay = new GregorianCalendar(
                    insertionPoint.getYear(),
                    insertionPoint.getMonth(),
                    insertionPoint.getDay().get(Calendar.DAY_OF_MONTH)
            );

            int daysToAppendOrPrepend = loadAfter ? i : i * -1;

            startDay.add(Calendar.DAY_OF_MONTH, daysToAppendOrPrepend);

            CalendarDay newDay = new CalendarDay(
                    startDay.get(Calendar.YEAR),
                    startDay.get(Calendar.MONTH),
                    startDay.get(Calendar.DAY_OF_MONTH));

            createdDays.add(newDay);
        }

        if (!loadAfter) Collections.reverse(createdDays);

        days.addAll(loadAfter? insertIdx + 1 : 0, createdDays);
        notifyItemRangeInserted(loadAfter? insertIdx + 1 : 0,10);
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DayViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.verticalweekcalendar_day, parent, false),
                resProvider,
                this);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalWeekAdapter.DayViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        day.setState(dateWatcher.getStateForDate(day.getYear(), day.getMonth(), day.getDay().get(Calendar.DAY_OF_MONTH), holder));
        holder.display(day);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public void setDateWatcher(DateWatcher dateWatcher) {
        this.dateWatcher = dateWatcher;
    }

    @Override
    public void onCalenderDayClicked(int year, int month, int day) {
        if (onDateClickListener!= null) onDateClickListener.onCalenderDayClicked(year,month,day);
    }

    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final String[] intToMonth = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        private final String[] intToWeekday = { "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        private final ResProvider resProvider;
        private final DateClickCallback clickCallback;

        private CalendarDay currentDay;

        public LinearLayout dayView;
        public TextView dayOfWeek;
        public TextView dayOfMonth;
        public TextView month;

        DayViewHolder(@NonNull View itemView, ResProvider resProvider, DateClickCallback clickCallback) {
            super(itemView);
            this.resProvider = resProvider;
            this.clickCallback = clickCallback;

            dayView = itemView.findViewById(R.id.container);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText);
            dayOfMonth = itemView.findViewById(R.id.dayOfMonthText);
            month = itemView.findViewById(R.id.MonthText);

            itemView.setOnClickListener(this);
        }

        void display(CalendarDay day){
            this.currentDay = day;
            dayView.invalidate();
            setupData(day);
            setupStyles(day);
        }

        private void setupStyles(CalendarDay day) {
            switch(day.getState()){
                case DEFAULT:
                    dayView.setBackground(ContextCompat.getDrawable(dayView.getContext(),resProvider.getDayBackground()));
                    dayOfMonth.setTypeface(resProvider.getCustomFont());
                    dayOfWeek.setTypeface(resProvider.getCustomFont());
                    month.setTypeface(resProvider.getCustomFont());

                    dayOfMonth.setTextColor(resProvider.getDayTextColor());
                    dayOfWeek.setTextColor(resProvider.getWeekDayTextColor());
                    month.setTextColor(resProvider.getDayTextColor());

                    break;
                case SELECTED:
                    dayView.setBackground(ContextCompat.getDrawable(dayView.getContext(),resProvider.getSelectedDayBackground()));
                    dayOfMonth.setTypeface(resProvider.getCustomFont());
                    dayOfWeek.setTypeface(resProvider.getCustomFont());
                    month.setTypeface(resProvider.getCustomFont());

                    dayOfMonth.setTextColor(resProvider.getSelectedDayTextColor());
                    dayOfWeek.setTextColor(resProvider.getSelectedDayTextColor());
                    month.setTextColor(resProvider.getSelectedDayTextColor());

                    break;
            }
        }

        private void setupData(CalendarDay day) {
            dayOfWeek.setText(intToWeekday[day.getDay().get(Calendar.DAY_OF_WEEK)-1]);
            dayOfMonth.setText(String.valueOf(day.getDay().get(Calendar.DAY_OF_MONTH)));
            month.setText(intToMonth[day.getDay().get(Calendar.MONTH)]);
        }

        @Override
        public void onClick(View view) {
            int year = currentDay.getYear();
            int month = currentDay.getMonth();
            int day = currentDay.getDay().get(Calendar.DAY_OF_MONTH);
            clickCallback.onCalenderDayClicked(year, month, day);
            notifyDataSetChanged();
        }
    }
}

