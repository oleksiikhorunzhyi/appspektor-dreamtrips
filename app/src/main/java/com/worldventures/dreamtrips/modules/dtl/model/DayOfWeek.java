package com.worldventures.dreamtrips.modules.dtl.model;

import com.innahema.collections.query.queriables.Queryable;

import java.util.Calendar;

public enum DayOfWeek {
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY),
    SUNDAY(Calendar.SUNDAY);

    private int calendarDayOfWeek;

    DayOfWeek(int calendarDayOfWeek) {
        this.calendarDayOfWeek = calendarDayOfWeek;
    }

    public static DayOfWeek from(int calendarDayOfWeek) {
        return Queryable.from(values()).first(element -> element.calendarDayOfWeek == calendarDayOfWeek);
    }
}
