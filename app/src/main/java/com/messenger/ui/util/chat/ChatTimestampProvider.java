package com.messenger.ui.util.chat;

import android.database.Cursor;

import com.messenger.entities.DataMessage$Table;

import javax.inject.Inject;

public class ChatTimestampProvider {

    private ChatTimestampFormatter timestampFormatter;

    @Inject
    public ChatTimestampProvider(ChatTimestampFormatter timestampFormatter) {
        this.timestampFormatter = timestampFormatter;
    }

    public String getTimestamp(Cursor cursor) {
        return timestampFormatter.getMessageTimestamp(cursor
                .getLong(cursor.getColumnIndex(DataMessage$Table.DATE)));
    }

    public boolean shouldShowAutomaticTimestamp(Cursor cursor) {
        int dateColumnIndex = cursor.getColumnIndex(DataMessage$Table.DATE);
        long currentDate = cursor.getLong(dateColumnIndex);
        long previousDate = 0;
        boolean moveCursorToPrev = cursor.moveToPrevious();
        if (moveCursorToPrev) {
            previousDate = cursor.getLong(dateColumnIndex);
        }
        cursor.moveToNext();
        int calendarDaysSincePreviousDate = 0;
        if (previousDate != 0) {
            calendarDaysSincePreviousDate = (int) ChatDateUtils
                    .calendarDaysBetweenDates(previousDate, currentDate);
        }
        if ((previousDate != 0 && calendarDaysSincePreviousDate > 0) || previousDate == 0) {
            return true;
        }
        return false;
    }
}
