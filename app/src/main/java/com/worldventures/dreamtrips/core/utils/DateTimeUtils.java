package com.worldventures.dreamtrips.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateTimeUtils {
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    public static final String FULL_SCREEN_PHOTO_DATE_FORMAT = "MMM dd, yyyy hh:mma";


    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return date != null ? sdf.format(date) : "";
    }

    public static String convertDateToString(int year, int month, int day) {
        return convertDateToString(year, month, day, DATE_FORMAT);
    }

    public static String convertDateToString(int year, int month, int day, String dateFormat) {
        SimpleDateFormat sim = new SimpleDateFormat(dateFormat, Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date date = calendar.getTime();
        return sim.format(date);
    }


    public static String convertTimeToString(int h, int m) {
        return convertTimeToString(h, m, TIME_FORMAT);
    }

    public static String convertTimeToString(int h, int m, String timeFormat) {
        SimpleDateFormat sim = new SimpleDateFormat(timeFormat, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, h);
        calendar.set(Calendar.MINUTE, m);
        Date date = calendar.getTime();
        return sim.format(date);
    }


    public static Date mergeDateTime(Date date, Date time) {
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(time);

        calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
        calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
        calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
        calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

        return calendarA.getTime();
    }

    public static Date dateFromString(String date) {
        return dateFromString(date, DATE_FORMAT);
    }

    public static Date timeFromString(String time) {
        return dateFromString(time, TIME_FORMAT);
    }

    public static Date dateFromString(String date, String dateFormat) {
        DateFormat result = new SimpleDateFormat(dateFormat, Locale.getDefault());
        try {
            return result.parse(date);
        } catch (ParseException e) {
            Timber.e(e, "");
        }
        return null;
    }

}
