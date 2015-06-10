package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class DateFilterItem implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
    }

    protected DateFilterItem(Parcel in) {
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
    }

    public static final Creator<DateFilterItem> CREATOR = new Creator<DateFilterItem>() {
        public DateFilterItem createFromParcel(Parcel source) {
            return new DateFilterItem(source);
        }

        public DateFilterItem[] newArray(int size) {
            return new DateFilterItem[size];
        }
    };
}
