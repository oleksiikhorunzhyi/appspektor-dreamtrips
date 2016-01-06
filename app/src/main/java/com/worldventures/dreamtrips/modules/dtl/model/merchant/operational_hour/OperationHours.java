package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import android.os.Parcel;

public class OperationHours {
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

}
