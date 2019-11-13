package com.emc.verticalweekcalendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emc.verticalweekcalendar.controller.CalendarDataSourceFactory;
import com.emc.verticalweekcalendar.controller.VerticalWeekAdapter;
import com.emc.verticalweekcalendar.interfaces.DateWatcher;
import com.emc.verticalweekcalendar.interfaces.OnDateClickListener;
import com.emc.verticalweekcalendar.interfaces.ResProvider;
import com.emc.verticalweekcalendar.model.CalendarDay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class VerticalWeekCalendar extends LinearLayoutCompat implements ResProvider {

    private String customFont;
    private int defaultDayTextColor;
    private int defaultWeekDayTextColor;
    private int defaultBackground;
    private int selectedTextColor;
    private int selectedBackground;

    private VerticalWeekAdapter adapter;
    private DateWatcher dateWatcher;
    private LifecycleOwner owner;

    public VerticalWeekCalendar(Context context) {
        super(context);
        initLayout(context);
    }

    public VerticalWeekCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        loadStyle(context,attrs);
    }

    public VerticalWeekCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
        loadStyle(context,attrs);
    }

    private void init(LifecycleOwner owner) {
        this.owner = owner;
        setupRecyclerView();
    }

    private void initLayout(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.verticalweekcalendar_week,this);
    }

    private void loadStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalWeekCalendar, R.attr.verticalWeekCalendarStyleAttr, R.style.VerticalWeekCalendarStyle);
        customFont = ta.getString(R.styleable.VerticalWeekCalendar_customFont);

        defaultBackground = ta.getResourceId(R.styleable.VerticalWeekCalendar_dayBackground, 0);
        defaultDayTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_dayTextColor, 0);
        defaultWeekDayTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_weekDayTextColor, 0);

        selectedBackground = ta.getResourceId(R.styleable.VerticalWeekCalendar_selectedBackground, 0);
        selectedTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_selectedDayTextColor, 0);

        ta.recycle();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView  = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(getAdapter());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm.findFirstVisibleItemPosition() < 15 ) {
                    getAdapter().records.add(0, createRows(15));
                    getAdapter().notifyItemInserted(0);
                    Log.i("onScrollChange", "FirstVisibleItem: " + lm.findFirstVisibleItemPosition());
                    Log.i("onScrollChange", "new Size: " + getAdapter().records.size());
                } else if ((getAdapter().records.size() - 1 - lm.findLastVisibleItemPosition()) < 15) {
                    getAdapter().records.add(getAdapter().records.size() - 1, createRows(15));
                    getAdapter().notifyItemInserted(getAdapter().records.size() - 1);
                    Log.i("onScrollChange", "LastVisibleItem: " + lm.findLastVisibleItemPosition());
                    Log.i("onScrollChange", "new Size: " + getAdapter().records.size());
                }
            }
        });
    }

    public VerticalWeekAdapter getAdapter() {

        return adapter == null ? createAdapter() : adapter;
    }

    public void refresh(){
        getAdapter().notifyDataSetChanged();
    }

    private VerticalWeekAdapter createAdapter() {

        final Executor executor = Executors.newFixedThreadPool(5);
        Calendar now = Calendar.getInstance();
        Calendar calendarTime = new GregorianCalendar(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        CalendarDataSourceFactory factory = new CalendarDataSourceFactory();
        factory.setDateWatcher(dateWatcher);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(5)
                .setPrefetchDistance(5)
                .build();

        LiveData<PagedList<CalendarDay>> days = new LivePagedListBuilder(factory,config)
                .setInitialLoadKey(calendarTime.getTime().getTime())
                .setFetchExecutor(executor)
                .build();

        adapter = new VerticalWeekAdapter(this);

        days.observe(owner, new Observer<PagedList<CalendarDay>>() {
            @Override
            public void onChanged(PagedList<CalendarDay> calendarDays) {
                adapter.submitList(calendarDays);
            }
        });

        return adapter;
    }

    public void setOnDateClickListener(final OnDateClickListener callback){
        getAdapter().setOnDateClickListener(callback);
    }

    private void setDateWatcher(DateWatcher dateWatcher) {
        this.dateWatcher = dateWatcher;
    }

    public void setCustomFont(String customFont) {
        this.customFont = customFont;
    }

    public void setDefaultDayTextColor(int defaultDayTextColor) {
        this.defaultDayTextColor = defaultDayTextColor;
    }

    public void setDefaultBackground(int defaultBackground) {
        this.defaultBackground = defaultBackground;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public void setSelectedBackground(int selectedBackground) {
        this.selectedBackground = selectedBackground;
    }

    @Override
    public int getSelectedDayBackground() {
        return selectedBackground;
    }

    @Override
    public int getSelectedDayTextColor() {
        return selectedTextColor;
    }

    @Override
    public int getDayTextColor() {
        return defaultDayTextColor;
    }

    @Override
    public int getWeekDayTextColor() {
        return defaultWeekDayTextColor;
    }

    @Override
    public int getDayBackground() {
        return defaultBackground;
    }

    @Override
    public Typeface getCustomFont() {
        if (customFont == null) {
            return null;
        }
        try {
            return Typeface.createFromAsset(getContext().getAssets(), "fonts/" + customFont);
        } catch (Exception e) {
            return null;
        }
    }

    public static class Builder {

        private int view;
        private DateWatcher dateWatcher;

        public Builder() {
        }

        public Builder setView(int view) {
            this.view = view;
            return this;
        }

        public Builder setDateWatcher(DateWatcher dateWatcher) {
            this.dateWatcher = dateWatcher;
            return this;
        }

        public VerticalWeekCalendar init(AppCompatActivity appCompatActivity){

            VerticalWeekCalendar calendar = appCompatActivity.findViewById(view);
            calendar.setDateWatcher(dateWatcher);
            calendar.init(appCompatActivity);
            return calendar;
        }
    }
}



