package com.worldventures.dreamtrips.core.utils;

import android.content.Context;

import com.worldventures.dreamtrips.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class DateTimeUtils {
    private static final String FILTER_PATTERN = "dd MMM yyyy";
    private static final int TOMORROW = 1;
    public static final int THIS_WEEK = 2;
    private static final int NEXT_WEEK = 3;
    public static final int THIS_MONTH = 4;
    private static final int NEXT_MONTH = 5;
    private static final int IN_SIX_MONTH = 6;
    private static final int THIS_YEAR = 7;
    private static final int NEXT_YEAR = 8;
    private static final int SOMETIME = 9;

    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    public static final String MEMBER_FORMAT = "MMM dd, hha";

    public static final String FULL_SCREEN_PHOTO_DATE_FORMAT = "MMM dd, yyyy hh:mma";
    public static final String DEFAULT_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateTimeUtils() {
    }

    public static String convertDateForFilters(Date date) {
        return convertDateToString(date, FILTER_PATTERN);
    }

    public static DateFormat getDefaultISOFormat() {
        return new SimpleDateFormat(DEFAULT_ISO_FORMAT, Locale.getDefault());
    }

    public static DateFormat[] getISO1DateFormats() {
        return new DateFormat[]{
                new SimpleDateFormat(DEFAULT_ISO_FORMAT, Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm.ss.SSS'Z'", Locale.getDefault()),
        };
    }

    public static String convertDateToString(Date date, DateFormat format) {
        if (date != null) {
            return format.format(date);
        } else {
            return null;
        }
    }

    public static String convertDateToJodaString(Date date, String format) {
        if (date != null) {
            DateTime dt = new DateTime(date);
            dt = dt.withZoneRetainFields(DateTimeZone.UTC);
            DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
            fmt = fmt.withZone(DateTimeZone.getDefault());
            return fmt.print(dt);
        } else {
            return null;
        }
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
        String[] dateArray = context.getResources().getStringArray(R.array.bucket_date_items);
        String today = context.getString(R.string.today);

        if (dateTarget == null) {
            return dateArray[SOMETIME];
        }

        Calendar calendar = Calendar.getInstance();
        DateTime dateTimeToday = new DateTime(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
        DateTime dateTimeTarget = new DateTime(dateTarget);


        if (dateTimeToday.isAfter(dateTimeTarget)) {
            return convertDateToString(dateTarget, DATE_FORMAT);
        }

        int daysBetween = Days.daysBetween(dateTimeToday, dateTimeTarget).getDays();

        int yearToday = dateTimeToday.get(DateTimeFieldType.year());
        int yearTarget = dateTimeTarget.get(DateTimeFieldType.year());

        boolean thisYear = yearToday == yearTarget;

        int weekOfYearToday = dateTimeToday.get(DateTimeFieldType.weekOfWeekyear());
        int weekOfYearTarget = dateTimeTarget.get(DateTimeFieldType.weekOfWeekyear());

        int monthToday = dateTimeToday.get(DateTimeFieldType.monthOfYear());
        int monthTarget = dateTimeTarget.get(DateTimeFieldType.monthOfYear());
        int monthsBetween = monthTarget - monthToday;

        String result;

        if (daysBetween == 0) {
            result = today;
        } else if (daysBetween == 1) {
            result = dateArray[TOMORROW];
        } else if (thisYear
                && weekOfYearToday
                == weekOfYearTarget) {
            result = dateArray[THIS_WEEK];
        } else if (thisYear &&
                weekOfYearTarget - weekOfYearToday == 1) {
            result = dateArray[NEXT_WEEK];
        } else if (thisYear
                && monthToday == monthTarget) {
            result = dateArray[THIS_MONTH];
        } else if (thisYear && monthsBetween == 1) {
            result = dateArray[NEXT_MONTH];
        } else if (monthsBetween > 1 && monthsBetween <= 6) {
            result = dateArray[IN_SIX_MONTH];
        } else if (thisYear) {
            result = dateArray[THIS_YEAR];
        } else if (yearTarget - yearToday == 1) {
            result = dateArray[NEXT_YEAR];
        } else {
            result = dateArray[SOMETIME];
        }

        return result;
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
            case THIS_YEAR:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
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
