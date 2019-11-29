package com.emc.verticalweekcalendar.controller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
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

    public static final DiffUtil.ItemCallback<CalendarDay> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CalendarDay>() {
                @Override
                public boolean areItemsTheSame(CalendarDay oldItem, CalendarDay newItem) {
                    return oldItem.getDay().getTimeInMillis() == newItem.getDay().getTimeInMillis();
                }
                @Override
                public boolean areContentsTheSame(CalendarDay oldItem, CalendarDay newItem) {
                    return oldItem.getDay().getTimeInMillis() == newItem.getDay().getTimeInMillis();
                }
            };

    public VerticalWeekAdapter(ResProvider resProvider) {
//        super(DIFF_CALLBACK);
        this.resProvider = resProvider;
        initCalendar();
    }

    private void initCalendar(){

        Calendar now = Calendar.getInstance();

        List<CalendarDay> createdDays = new ArrayList<>();
        for(int i = 0; i <= 7; i++) {
            Calendar today = new GregorianCalendar(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            today.add(Calendar.DAY_OF_MONTH, i * -1);

            CalendarDay createdDay = new CalendarDay(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));
            Log.i("initCalendar", createdDay.toString() + " " + i + " " + today.get(Calendar.DAY_OF_MONTH));
            createdDays.add(createdDay);
        }
        Collections.reverse(createdDays);
        days.addAll(createdDays);

        for(int i = 1; i <= 7; i++) {
            Calendar today = new GregorianCalendar(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            today.add(Calendar.DAY_OF_MONTH, i);

            CalendarDay createdDay = new CalendarDay(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH));
            Log.i("initCalendar", createdDay.toString() + " " + i + " " + today.get(Calendar.DAY_OF_MONTH));
            days.add(createdDay);
        }
    }

    public List<CalendarDay> addCalendarDays(boolean loadAfter) {

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
            Log.i("createdDays", "Day: " + startDay.get(Calendar.DAY_OF_MONTH) + " " + loadAfter);
            startDay.add(Calendar.DAY_OF_MONTH, daysToAppendOrPrepend);

            CalendarDay newDay = new CalendarDay(
                    startDay.get(Calendar.YEAR),
                    startDay.get(Calendar.MONTH),
                    startDay.get(Calendar.DAY_OF_MONTH));
//            if (dateWatcher != null) newDay.setState(getDateState(newDay.getYear(),newDay.getMonth(),newDay.getDay().get(Calendar.DAY_OF_MONTH)));

            createdDays.add(newDay);
        }
        if (!loadAfter) Collections.reverse(createdDays);

        Log.i("addCalendarDays1", "Size: " + days.size() + "insertIdx: " + insertIdx + " " + createdDays.toString());
        days.addAll(loadAfter? insertIdx + 1 : 0, createdDays);
        Log.i("addCalendarDays2", "Size: " + days.size() + " " + days.toString());
        notifyItemRangeInserted(loadAfter? insertIdx + 1 : 0,10);
//        notifyItemInserted(insertIdx);
        return createdDays;
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
        Log.i("onBindViewHolder", day.toString() + " " + position);
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

    @Override
    public void onCalenderDayClicked(int year, int month, int day) {
        if (onDateClickListener!= null) onDateClickListener.onCalenderDayClicked(year,month,day);
//        recyclerView.scrollToPosition(22);
//        notifyDataSetChanged();
    }

    class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final String[] intToMonth = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        private final String[] intToWeekday = { "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        private final ResProvider resProvider;
        private final DateClickCallback clickCallback;

        private CalendarDay currentDay;

        private LinearLayout parent;
        private TextView dayOfWeek;
        private TextView dayOfMonth;
        private TextView month;

        DayViewHolder(@NonNull View itemView, ResProvider resProvider, DateClickCallback clickCallback) {
            super(itemView);
            this.resProvider = resProvider;
            this.clickCallback = clickCallback;

            parent = (LinearLayout) itemView.findViewById(R.id.container);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText);
            dayOfMonth = itemView.findViewById(R.id.dayOfMonthText);
            month = itemView.findViewById(R.id.MonthText);

            itemView.setOnClickListener(this);
        }

        void display(CalendarDay day){
            this.currentDay = day;
            parent.invalidate();
            setupData(day);
            setupStyles(day);
        }

        private void setupStyles(CalendarDay day) {
            switch(day.getState()){
                case DEFAULT:
                    parent.setBackground(ContextCompat.getDrawable(parent.getContext(),resProvider.getDayBackground()));
                    dayOfMonth.setTypeface(resProvider.getCustomFont());
                    dayOfWeek.setTypeface(resProvider.getCustomFont());
                    month.setTypeface(resProvider.getCustomFont());

                    dayOfMonth.setTextColor(resProvider.getDayTextColor());
                    dayOfWeek.setTextColor(resProvider.getWeekDayTextColor());
                    month.setTextColor(resProvider.getDayTextColor());

                    break;
                case SELECTED:
                    parent.setBackground(ContextCompat.getDrawable(parent.getContext(),resProvider.getSelectedDayBackground()));
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
        }
    }

}

