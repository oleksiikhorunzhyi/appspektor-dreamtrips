package com.messenger.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChatDateUtils {
    private final static int MAX_YEAR = Calendar.getInstance().getMaximum(Calendar.YEAR);

    public static Calendar getToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        return today;
    }

    public static long calendarDaysBetweenDates(Date startDate, Date dateEnd) {
        return calendarDaysBetweenDates(startDate.getTime(), dateEnd.getTime());
    }

    public static long calendarDaysBetweenDates(long startDate, long dateEnd) {
        Calendar start = Calendar.getInstance();
        start.setTimeZone(TimeZone.getDefault());
        start.setTimeInMillis(startDate);

        Calendar end = Calendar.getInstance();
        end.setTimeZone(TimeZone.getDefault());
        end.setTimeInMillis(dateEnd);

        // Set the copies to be at midnight, but keep the day information.

        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.

        long diff = end.getTimeInMillis() - start.getTimeInMillis();

        return TimeUnit.MILLISECONDS.toDays(
                Math.abs(diff));
    }

    public static long getErrorMessageDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, MAX_YEAR);
        return calendar.getTimeInMillis();
    }
}
