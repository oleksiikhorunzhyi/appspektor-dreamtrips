package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class DtlMerchantsFilterAttribute implements Parcelable, Comparable<DtlMerchantsFilterAttribute> {

    private String attributeName;
    private Boolean checked;

    public DtlMerchantsFilterAttribute() {
    }

    public DtlMerchantsFilterAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    protected DtlMerchantsFilterAttribute(Parcel in) {
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

        DtlMerchantsFilterAttribute that = (DtlMerchantsFilterAttribute) o;

        return !(attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null);
    }

    @Override
    public int hashCode() {
        return attributeName != null ? attributeName.hashCode() : 0;
    }

    @Override
    public int compareTo(@NonNull DtlMerchantsFilterAttribute another) {
        return attributeName.compareToIgnoreCase(another.attributeName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<DtlMerchantsFilterAttribute> CREATOR = new Creator<DtlMerchantsFilterAttribute>() {
        @Override
        public DtlMerchantsFilterAttribute createFromParcel(Parcel in) {
            return new DtlMerchantsFilterAttribute(in);
        }

        @Override
        public DtlMerchantsFilterAttribute[] newArray(int size) {
            return new DtlMerchantsFilterAttribute[size];
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
