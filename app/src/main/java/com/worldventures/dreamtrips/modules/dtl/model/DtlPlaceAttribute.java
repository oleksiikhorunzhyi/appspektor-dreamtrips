package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlPlaceAttribute implements Parcelable {

    private String name;

    public DtlPlaceAttribute() {
    }

    public String getName() {
        return name;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    protected DtlPlaceAttribute(Parcel in) {
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DtlPlaceAttribute> CREATOR = new Creator<DtlPlaceAttribute>() {
        @Override
        public DtlPlaceAttribute createFromParcel(Parcel in) {
            return new DtlPlaceAttribute(in);
        }

        @Override
        public DtlPlaceAttribute[] newArray(int size) {
            return new DtlPlaceAttribute[size];
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public String toString() {
        return name;
    }
}
