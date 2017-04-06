package com.worldventures.dreamtrips.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {

    public static final String DEFAULT_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_ISO_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DEFAULT_ISO_FORMAT_DATE_ONLY = "yyyy-MM-dd";

    private DateTimeUtils() {
    }

    public static DateFormat[] getISO1DateFormats() {
        return new DateFormat[]{
                new SimpleDateFormat(DEFAULT_ISO_FORMAT_WITH_TIMEZONE),
                new SimpleDateFormat(DEFAULT_ISO_FORMAT),
                new SimpleDateFormat(DEFAULT_ISO_FORMAT_DATE_ONLY),
        };
    }

    public static String convertDateToString(String pattern, Date date) {
        // DateTime treats null as now
        if (date == null) return null;

        return DateTimeFormat.forPattern(pattern)
                .withLocale(Locale.US)
                .withZone(DateTimeZone.UTC)
                .print(new DateTime(date));
    }


}
