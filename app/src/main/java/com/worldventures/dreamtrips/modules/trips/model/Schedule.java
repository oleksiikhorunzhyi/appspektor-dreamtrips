package com.worldventures.dreamtrips.modules.trips.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Schedule implements Serializable {

    private static final String PATTERN_MONTH_AND_DAY = "MMM d";
    private static final String PATTERN_DAY = "d";

    private final static SimpleDateFormat simpleDateFormatMonthDay = new SimpleDateFormat(PATTERN_MONTH_AND_DAY, Locale.getDefault());
    private final static SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat(PATTERN_DAY, Locale.getDefault());

    @SerializedName("start_on")
    private Date startOn;
    @SerializedName("end_on")
    private Date endOn;

    public Schedule() {
    }

    public java.util.Date getStartDate() {
        return startOn;
    }

    public void setStartDate(java.util.Date startOn) {
        this.startOn = startOn;
    }

    public java.util.Date getEndDate() {
        return endOn;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endOn = endDate;
    }

    public boolean check(DateFilterItem dateFilterItem) {
        return startOn.after(dateFilterItem.getStartDate()) && endOn.before(dateFilterItem.getEndDate());
    }

    @Override
    public String toString() {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(startOn.getTime());
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(endOn.getTime());

        StringBuilder builder = new StringBuilder();
        builder.append(simpleDateFormatMonthDay.format(getStartDate()));
        builder.append(" - ");
        builder.append(calendarEnd.get(Calendar.MONTH) != calendarStart.get(Calendar.MONTH) ?
                simpleDateFormatMonthDay.format(getEndDate()) :
                simpleDateFormatDay.format(getEndDate()));
        return builder.toString();
    }
}
