package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class DtlPlacesFilterAttribute implements Parcelable, Comparable<DtlPlacesFilterAttribute> {

    private String attributeName;
    private Boolean checked;

    public DtlPlacesFilterAttribute() {
    }

    public DtlPlacesFilterAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    protected DtlPlacesFilterAttribute(Parcel in) {
        attributeName = in.readString();
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isChecked() {
        return checked == null ? true : checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return attributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlPlacesFilterAttribute that = (DtlPlacesFilterAttribute) o;

        return !(attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null);
    }

    @Override
    public int hashCode() {
        return attributeName != null ? attributeName.hashCode() : 0;
    }

    @Override
    public int compareTo(@NonNull DtlPlacesFilterAttribute another) {
        return attributeName.compareToIgnoreCase(another.attributeName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<DtlPlacesFilterAttribute> CREATOR = new Creator<DtlPlacesFilterAttribute>() {
        @Override
        public DtlPlacesFilterAttribute createFromParcel(Parcel in) {
            return new DtlPlacesFilterAttribute(in);
        }

        @Override
        public DtlPlacesFilterAttribute[] newArray(int size) {
            return new DtlPlacesFilterAttribute[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attributeName);
    }
}
