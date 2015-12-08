package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

/**
 * Bundle to be supplied when navigating to {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment}
 */
public class MerchantsMapBundle implements Parcelable {

    private DtlLocation location;
    private boolean isSlave;

    public MerchantsMapBundle(DtlLocation location, boolean isSlave) {
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

    protected MerchantsMapBundle(Parcel in) {
        location = in.readParcelable(DtlLocation.class.getClassLoader());
        isSlave = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeByte(isSlave ? (byte) 1 : 0);
    }

    public static final Creator<MerchantsMapBundle> CREATOR = new Creator<MerchantsMapBundle>() {
        @Override
        public MerchantsMapBundle createFromParcel(Parcel in) {
            return new MerchantsMapBundle(in);
        }

        @Override
        public MerchantsMapBundle[] newArray(int size) {
            return new MerchantsMapBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
