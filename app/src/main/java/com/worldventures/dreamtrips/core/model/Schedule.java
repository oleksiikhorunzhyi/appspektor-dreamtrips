package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;

public class Schedule implements Serializable {

    java.util.Date startDate;
    java.util.Date endDate;

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }
}
