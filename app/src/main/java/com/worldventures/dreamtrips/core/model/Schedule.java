package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Schedule implements Serializable {

    private static final String patternFirst = "MMM d";
    private static final String patternSecond = "d yyyy";


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
        SimpleDateFormat simpleDateFormatfirst = new SimpleDateFormat(patternFirst, Locale.US);
        SimpleDateFormat simpleDateFormatsecond = new SimpleDateFormat(patternSecond, Locale.US);
        StringBuilder builder = new StringBuilder();
        builder.append(simpleDateFormatfirst.format(getStartDate()));
        builder.append(" - ");
        builder.append(simpleDateFormatsecond.format(getEndDate()));
        return builder.toString();
    }
}
