package com.messenger.util;

import android.content.Context;
import android.text.format.DateUtils;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

public class ChatTimestampFormatter {

    private Context context;

    private SimpleDateFormat timeDateFormatter;
    private SimpleDateFormat dayOfTheWeekDateFormatter;
    private SimpleDateFormat dayOfTheMonthDateFormatter;

    @Inject
    public ChatTimestampFormatter(@ForApplication Context context) {
        this.context = context;
        this.timeDateFormatter = new SimpleDateFormat("h:mm aa");
        this.dayOfTheWeekDateFormatter = new SimpleDateFormat("EEEE");
        this.dayOfTheMonthDateFormatter = new SimpleDateFormat("MMM dd");
    }

    public String getMessageTimestamp(long currentDate) {
        StringBuilder sb = new StringBuilder();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(currentDate);
        Calendar todayMidnightCalendar = ChatDateUtils.getToday();
        long todayMidnightTimestamp = todayMidnightCalendar.getTime().getTime();

        int daysSinceToday = (int) ChatDateUtils
                .calendarDaysBetweenDates(todayMidnightTimestamp, currentDate);
        boolean thisYear = dateCalendar.get(Calendar.YEAR) == todayMidnightCalendar.get(Calendar.YEAR);

        if (daysSinceToday == 0) {
            sb.append(context.getString(R.string.chat_list_date_entry_today));
        } else if (daysSinceToday == 1) {
            sb.append(context.getString(R.string.chat_list_date_entry_yesterday));
        } else if (daysSinceToday > 1 && daysSinceToday < 7) {
            sb.append(dayOfTheWeekDateFormatter.format(currentDate));
        } else if (dateCalendar.get(Calendar.YEAR) == todayMidnightCalendar.get(Calendar.YEAR)) {
            sb.append(dayOfTheMonthDateFormatter.format(currentDate));
        } else {
            sb.append(DateUtils.getRelativeDateTimeString(context, currentDate,
                    DateUtils.HOUR_IN_MILLIS, DateUtils.YEAR_IN_MILLIS, 0));
        }

        if (thisYear) {
            sb.append(", ");
            sb.append(timeDateFormatter.format(currentDate));
        }

        return sb.toString();
    }
}
