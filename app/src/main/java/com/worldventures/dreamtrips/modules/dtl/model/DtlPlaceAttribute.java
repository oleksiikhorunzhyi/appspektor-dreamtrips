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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlPlaceAttribute that = (DtlPlaceAttribute) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
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
