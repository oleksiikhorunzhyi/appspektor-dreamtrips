package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bundle to be supplied when navigating to {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMapFragment}
 */
@Deprecated
// TODO : remove in favor of parameter in NavigationConfigBuilder
public class DtlMapBundle implements Parcelable {

    private boolean isSlave;

    public DtlMapBundle(boolean isSlave) {
        this.isSlave = isSlave;
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
        isSlave = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
