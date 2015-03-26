package com.worldventures.dreamtrips.modules.tripsimages.model;

import java.util.Date;

public class DateTime extends Date {

    public DateTime() {
    }

    public DateTime(int year, int month, int day) {
        super(year, month, day);
    }

    public DateTime(int year, int month, int day, int hour, int minute) {
        super(year, month, day, hour, minute);
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        super(year, month, day, hour, minute, second);
    }

    public DateTime(long milliseconds) {
        super(milliseconds);
    }

    public DateTime(String string) {
        super(string);
    }

    public DateTime(Date date) {
        super(date.getTime());
    }
}
