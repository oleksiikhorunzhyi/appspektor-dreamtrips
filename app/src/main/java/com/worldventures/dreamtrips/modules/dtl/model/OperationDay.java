package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OperationDay implements Parcelable {

    DayOfWeek dayOfWeek;
    List<OperationHours> operationHours;

    public OperationDay() {
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public List<OperationHours> getOperationHours() {
        return operationHours;
    }

    public boolean openNow() {
        Calendar calendar = Calendar.getInstance();

        Date today = calendar.getTime();

        boolean isOpened = false;

        if (operationHours != null) {
            for (OperationHours operationHour : operationHours) {
                Date timeOpened = DateTimeUtils.mergeDateTime(today, DateTimeUtils.timeFromString(operationHour.getStartTime()));
                Date timeClosed = DateTimeUtils.mergeDateTime(today, DateTimeUtils.timeFromString(operationHour.getEndTime()));

                if (today.after(timeOpened) && today.before(timeClosed)) {
                    isOpened = true;
                    break;
                }
            }
        }

        return isOpened;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDay that = (OperationDay) o;

        return dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return dayOfWeek != null ? dayOfWeek.hashCode() : 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected OperationDay(Parcel in) {
        dayOfWeek = (DayOfWeek) in.readSerializable();
        operationHours = in.createTypedArrayList(OperationHours.CREATOR);
    }

    public static final Creator<OperationDay> CREATOR = new Creator<OperationDay>() {
        @Override
        public OperationDay createFromParcel(Parcel in) {
            return new OperationDay(in);
        }

        @Override
        public OperationDay[] newArray(int size) {
            return new OperationDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(dayOfWeek);
        dest.writeTypedList(operationHours);
    }

}
