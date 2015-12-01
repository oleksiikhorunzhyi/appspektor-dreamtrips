package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlOfferDescription implements Parcelable {

    String description;

    public DtlOfferDescription() {
    }

    public String getDescription() {
        return description;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlOfferDescription(Parcel in) {
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
    }

    public static final Creator<DtlOfferDescription> CREATOR = new Creator<DtlOfferDescription>() {
        @Override
        public DtlOfferDescription createFromParcel(Parcel in) {
            return new DtlOfferDescription(in);
        }

        @Override
        public DtlOfferDescription[] newArray(int size) {
            return new DtlOfferDescription[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
