package com.worldventures.dreamtrips.core.utils;

import android.content.Context;

import com.worldventures.dreamtrips.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

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
    public static final int THIS_WEEK = 2;
    private static final int NEXT_WEEK = 3;
    public static final int THIS_MONTH = 4;
    private static final int NEXT_MONTH = 5;
    private static final int IN_SIX_MONTH = 6;
    private static final int NEXT_YEAR = 7;
    private static final int SOMETIME = 8;

    public static final int FULL_DAY = 1000 * 60 * 60 * 24;
    public static final int NEXT_WEEK_MILLIS = 7 * FULL_DAY;

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

    public static String convertDateToReference(Context context, Date dateTarget) {
        if (dateTarget == null) {
            return convertDateToString(dateTarget, DATE_FORMAT);
        }

        String[] dateArray = context.getResources().getStringArray(R.array.bucket_date_items);

        DateTime dateTimeToday = new DateTime(Calendar.getInstance().getTime());
        DateTime dateTimeTarget = new DateTime(dateTarget);

        if (dateTimeToday.isAfter(dateTimeTarget)) {
            return convertDateToString(dateTarget, DATE_FORMAT);
        }

        int daysBetween = Days.daysBetween(dateTimeToday, dateTimeTarget).getDays();
        int weeksBetween = Weeks.weeksBetween(dateTimeToday, dateTimeTarget).getWeeks();
        int monthsBetween = Months.monthsBetween(dateTimeToday, dateTimeTarget).getMonths();
        int yearsBetween = Years.yearsBetween(dateTimeToday, dateTimeTarget).getYears();

        if (daysBetween == 0) {
            return context.getString(R.string.today);
        } else if (daysBetween == 1) {
            return dateArray[TOMORROW];
        } else if (daysBetween > 1 && weeksBetween == 0) {
            return dateArray[THIS_WEEK];
        } else if (weeksBetween == 1) {
            return dateArray[NEXT_WEEK];
        } else if (monthsBetween == 0) {
            return dateArray[THIS_MONTH];
        } else if (monthsBetween == 1) {
            return dateArray[NEXT_MONTH];
        } else if (monthsBetween > 1 && monthsBetween <= 12) {
            return dateArray[IN_SIX_MONTH];
        } else if (yearsBetween == 1) {
            return dateArray[NEXT_YEAR];
        } else {
            return dateArray[SOMETIME];
        }
    }

    public static Date convertReferenceToDate(int position) {
        Calendar calendar = Calendar.getInstance();

        switch (position) {
            case TOMORROW:
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
                break;
            case THIS_WEEK:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                break;
            case NEXT_WEEK:
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                break;
            case THIS_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case NEXT_MONTH:
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case IN_SIX_MONTH:
                calendar.add(Calendar.MONTH, 6);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case NEXT_YEAR:
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.MONTH, 1);
                break;
            default:
                break;
        }

        return calendar.getTime();
    }

}
