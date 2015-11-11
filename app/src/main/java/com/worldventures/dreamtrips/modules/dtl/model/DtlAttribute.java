package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlAttribute implements Parcelable{

    private String attributeName;
    private Boolean checked;

    public DtlAttribute() {
    }

    public DtlAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    protected DtlAttribute(Parcel in) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlAttribute that = (DtlAttribute) o;

        return !(attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null);
    }

    @Override
    public int hashCode() {
        return attributeName != null ? attributeName.hashCode() : 0;
    }
///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    public static final Creator<DtlAttribute> CREATOR = new Creator<DtlAttribute>() {
        @Override
        public DtlAttribute createFromParcel(Parcel in) {
            return new DtlAttribute(in);
        }

        @Override
        public DtlAttribute[] newArray(int size) {
            return new DtlAttribute[size];
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
