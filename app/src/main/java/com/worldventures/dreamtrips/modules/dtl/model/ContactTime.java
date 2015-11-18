package com.worldventures.dreamtrips.modules.dtl.model;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;

public class ContactTime {

    private String from;
    private String to;

    public ContactTime(long from, long to) {
        this.from = DateTimeUtils.convertDateToUTCString(new Date(from));
        this.to = DateTimeUtils.convertDateToUTCString(new Date(to));
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
