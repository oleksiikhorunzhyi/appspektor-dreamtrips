package com.worldventures.dreamtrips.core.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Edward on 08.02.15.
 */
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
        startDate = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
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
