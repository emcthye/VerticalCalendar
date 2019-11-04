package com.emc.verticalweekcalendar.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
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
import java.util.List;

import static com.emc.verticalweekcalendar.model.CalendarDay.DEFAULT;
import static com.emc.verticalweekcalendar.model.CalendarDay.SELECTED;

public class VerticalWeekAdapter extends PagedListAdapter<CalendarDay,DayViewHolder> implements DateClickCallback {

    private List<CalendarDay> days = new ArrayList<>();
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
        super(DIFF_CALLBACK);
        this.resProvider = resProvider;
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
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay day = getItem(position);
        Log.i("onBindViewHolder", day.toString() + " " + position);
        holder.display(day);
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
        notifyDataSetChanged();
    }

}

class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static final String[] intToMonth = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private static final String[] intToWeekday = { "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

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