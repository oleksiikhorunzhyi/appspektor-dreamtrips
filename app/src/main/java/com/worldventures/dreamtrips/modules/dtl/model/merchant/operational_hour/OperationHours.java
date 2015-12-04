package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import android.os.Parcel;
import android.os.Parcelable;

public class OperationHours implements Parcelable {
    String from;
    String to;

    public OperationHours() {
    }

    protected OperationHours(Parcel in) {
        from = in.readString();
        to = in.readString();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
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
        dest.writeString(from);
        dest.writeString(to);
    }
}
