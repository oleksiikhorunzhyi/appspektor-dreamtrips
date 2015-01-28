package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Schedule implements Serializable {

    private static final String PATTERN_MONTH_AND_DAY = "MMM d";
    private static final String PATTERN_DAY = "d";


    java.util.Date start_on;
    java.util.Date end_on;

    public java.util.Date getStartDate() {
        return start_on;
    }

    public void setStartDate(java.util.Date startDate) {
        this.start_on = startDate;
    }

    public java.util.Date getEndDate() {
        return end_on;
    }

    public void setEndDate(java.util.Date endDate) {
        this.end_on = endDate;
    }

    @Override
    public String toString() {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(start_on.getTime());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(end_on.getTime());

        SimpleDateFormat simpleDateFormatFirst = new SimpleDateFormat(PATTERN_MONTH_AND_DAY, Locale.US);
        SimpleDateFormat simpleDateFormatSecond = new SimpleDateFormat(calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH)
                ? PATTERN_MONTH_AND_DAY : PATTERN_DAY, Locale.US);

        StringBuilder builder = new StringBuilder();
        builder.append(simpleDateFormatFirst.format(getStartDate()));
        builder.append(" - ");
        builder.append(simpleDateFormatSecond.format(getEndDate()));
        return builder.toString();
    }
}
