package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OperationHours implements Parcelable {
    String startTime;
    String endTime;

    public OperationHours() {
    }

    protected OperationHours(Parcel in) {
        startTime = in.readString();
        endTime = in.readString();
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<OperationHours> CREATOR = new Creator<OperationHours>() {
        @Override
        public OperationHours createFromParcel(Parcel in) {
            return new OperationHours(in);
        }

        @Override
        public OperationHours[] newArray(int size) {
            return new OperationHours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startTime);
        dest.writeString(endTime);
    }
}
