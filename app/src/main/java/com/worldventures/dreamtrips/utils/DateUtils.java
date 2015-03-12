package com.worldventures.dreamtrips.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 1 on 08.02.15.
 */
public class DateUtils {

    private static final String FILTER_PATTERN = "dd MMM yyyy";

    public static String convertDateForFilters(int year, int day, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return convertDate(calendar.getTime(), FILTER_PATTERN);
    }


    public static String convertDateForFilters(Date date) {
        return convertDate(date, FILTER_PATTERN);
    }

    private static String convertDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String convertSecondsToString(int seconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(seconds * 1000L));
    }


}
