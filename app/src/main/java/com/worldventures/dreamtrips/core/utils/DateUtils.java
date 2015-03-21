package com.worldventures.dreamtrips.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String FILTER_PATTERN = "dd MMM yyyy";

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
