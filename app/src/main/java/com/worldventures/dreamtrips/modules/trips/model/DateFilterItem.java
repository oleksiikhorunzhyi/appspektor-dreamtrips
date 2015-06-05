package com.worldventures.dreamtrips.modules.trips.model;

import java.util.Calendar;
import java.util.Date;

public class DateFilterItem {

    private Date startDate;
    private Date endDate;

    public DateFilterItem() {
        reset();
    }

    public DateFilterItem(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void reset() {
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 12);
        endDate = calendar.getTime();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
