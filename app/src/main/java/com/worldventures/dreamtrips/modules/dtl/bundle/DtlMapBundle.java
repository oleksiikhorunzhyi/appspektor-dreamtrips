package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

/**
 * Bundle to be supplied when navigating to {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment}
 */
public class DtlMapBundle implements Parcelable {

    private DtlLocation location;
    private boolean isSlave;

    public DtlMapBundle(DtlLocation location, boolean isSlave) {
        this.location = location;
        this.isSlave = isSlave;
    }

    public DtlLocation getLocation() {
        return location;
    }

    public void setLocation(DtlLocation location) {
        this.location = location;
    }

    public boolean isSlave() {
        return isSlave;
    }

    public void setIsSlave(boolean isSlave) {
        this.isSlave = isSlave;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMapBundle(Parcel in) {
        location = in.readParcelable(DtlLocation.class.getClassLoader());
        isSlave = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeByte(isSlave ? (byte) 1 : 0);
    }

    public static final Creator<DtlMapBundle> CREATOR = new Creator<DtlMapBundle>() {
        @Override
        public DtlMapBundle createFromParcel(Parcel in) {
            return new DtlMapBundle(in);
        }

        @Override
        public DtlMapBundle[] newArray(int size) {
            return new DtlMapBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
