# VerticalCalendar
Scroll calendar days infinitely in a vertical column.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/emcthye/VerticalCalendar/blob/master/LICENSE)

![](app/demo.gif)

## Installation

#### Import with Gradle:

```
dependencies {
  implementation 'com.emc.thye:VerticalCalendar:1.0.4'
}
```
## Basic Usage
```xml
<com.emc.verticalweekcalendar.VerticalWeekCalendar
    android:id="@+id/verticalCalendar"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    app:customFont="quicksand_bold"
    app:selectedBackground="@color/colorSelectedDayBG"/>
```
```java
VerticalWeekCalendar calendarView = new VerticalWeekCalendar.Builder()
        .setView(R.id.verticalCalendar)
        .init(this);
```
### Date Click Listener

```java
private GregorianCalendar selected;

calendarView.setOnDateClickListener(new OnDateClickListener() {
    @Override
    public void onCalenderDayClicked(int year, int month, int day) {
        GregorianCalendar selectedDay = new GregorianCalendar(year, month, day);
        if(selected.compareTo(selectedDay) != 0) { 
          //Not the same day is clicked
            selected = selectedDay;
        }
    }
});
```
### Date Watcher
There are two built-in state, <em>SELECTED</em> & <em>DEFAULT</em> that changes the day view's background color. You can also implement your own logic by returning **CalendarDay.DEFAULT** as the state and use **VerticalWeekAdapter.DayViewHolder** view as the reference to change the view properties.
```java
calendarView.setDateWatcher(new DateWatcher() {
    @Override
    public int getStateForDate(int year, int month, int day, VerticalWeekAdapter.DayViewHolder view) {
        view.dayView.setBackgroundColor(Color.parseColor("#ff4000"));
        return selected.compareTo(new GregorianCalendar(year, month, day)) == 0 ?
                CalendarDay.SELECTED : CalendarDay.DEFAULT;
    }
});
```
## Customization

#### Day Text

* customFont - **.otf**/**.ttf** placed in **res/font** folder
* dayTextColor
* weekDayTextColor
* selectedDayTextColor

#### Day Background

* dayBackground
* selectedBackground
