package com.worldventures.dreamtrips.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateTimeUtils {
    private static final String FILTER_PATTERN = "dd MMM yyyy";
    private static final int TOMORROW = 1;
    private static final int NEXT_WEEK = 2;
    private static final int NEXT_MONTH = 3;
    private static final int IN_SIX_MONTH = 4;
    private static final int NEXT_YEAR = 5;

    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";

    public static final String FULL_SCREEN_PHOTO_DATE_FORMAT = "MMM dd, yyyy hh:mma";
    public static final String DEFAULT_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";



    public static String convertDateForFilters(Date date) {
        return convertDateToString(date, FILTER_PATTERN);
    }

    public static DateFormat getDefaultISOFormat() {
        return new SimpleDateFormat(DEFAULT_ISO_FORMAT, Locale.getDefault());
    }

    public static DateFormat[] getISO1DateFormats() {
        return new DateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
                new SimpleDateFormat(DEFAULT_ISO_FORMAT, Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSS'Z'", Locale.getDefault()),
        };
    }

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        if (date != null) {
            return sdf.format(date);
        } else {
            return null;
        }
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

    public static String convertSecondsToString(int seconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return formatter.format(new Date(seconds * 1000L));
    }

    public static Date dateFromString(String date) {
        return dateFromString(date, DATE_FORMAT);
    }

    public static Date timeFromString(String time) {
        return dateFromString(time, TIME_FORMAT);
    }

    public static Date dateFromString(String date, String dateFormat) {
        if (date != null) {
            DateFormat result = new SimpleDateFormat(dateFormat, Locale.getDefault());
            try {
                return result.parse(date);
            } catch (ParseException e) {
                Timber.e(e, "");
            }
        }
        return null;
    }

    public static Date convertReferenceToDate(int position) {
        Calendar calendar = Calendar.getInstance();

        switch (position) {
            case TOMORROW:
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                break;
            case NEXT_WEEK:
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
                break;
            case NEXT_MONTH:
                calendar.add(Calendar.MONTH, 1);
                break;
            case IN_SIX_MONTH:
                calendar.add(Calendar.MONTH, 6);
                break;
            case NEXT_YEAR:
                calendar.add(Calendar.MONTH, 12);
                break;
            default:
                break;
        }

        return calendar.getTime();
    }

}
