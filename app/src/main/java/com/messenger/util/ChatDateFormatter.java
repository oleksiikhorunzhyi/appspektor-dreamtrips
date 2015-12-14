package com.messenger.util;

import android.content.Context;

import com.messenger.model.ChatMessage;
import com.worldventures.dreamtrips.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChatDateFormatter {

    private SimpleDateFormat todayDateFormat;
    private SimpleDateFormat moreThanTwoDaysAgoFormat;

    private Context context;

    public ChatDateFormatter(Context context) {
        this.context = context;
        todayDateFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_today));
        moreThanTwoDaysAgoFormat = new SimpleDateFormat(context
            .getString(R.string.conversation_list_last_message_date_format_more_than_one_day_ago));
    }

    public String formatLastConversationMessage(ChatMessage chatMessage) {
        Calendar messageDate = Calendar.getInstance();
        messageDate.setTime(chatMessage.getDate());

        Calendar today = getToday();

        if (messageDate.getTime().after(today.getTime())) {
            return todayDateFormat.format(chatMessage.getDate());
        } else {
            Calendar yesterday = today;
            yesterday.roll(Calendar.DAY_OF_YEAR, false);
            if (messageDate.getTime().after(yesterday.getTime())) {
                return context.getString(R.string.conversation_list_last_message_date_format_yesterday);
            } else {
                return moreThanTwoDaysAgoFormat.format(chatMessage.getDate());
            }
        }
    }

    public static Calendar getToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        return today;
    }

    @Deprecated
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
}
