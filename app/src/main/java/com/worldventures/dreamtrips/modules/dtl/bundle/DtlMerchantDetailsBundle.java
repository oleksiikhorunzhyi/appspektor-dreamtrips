package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantDetailsFragment;

/**
 * Bundle to be supplied when navigating to {@link DtlMerchantDetailsFragment}
 */
public class DtlMerchantDetailsBundle implements Parcelable {

    private String id;
    /**
     * Set to false when showing details as separate activity, true when showing as a fragment in current screen hierarchy
     */
    private boolean isSlave;

    /**
     * @param id merchant id
     * @param isSlave indicator that screen is slave (shown in current view hierarchy) or master (new activity)
     */
    public DtlMerchantDetailsBundle(String id, boolean isSlave) {
        this.id = id;
        this.isSlave = isSlave;
    }

    public String getId() {
        return id;
    }

    public boolean isSlave() {
        return isSlave;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMerchantDetailsBundle(Parcel in) {
        id = in.readString();
        isSlave = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (isSlave ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DtlMerchantDetailsBundle> CREATOR = new Creator<DtlMerchantDetailsBundle>() {
        @Override
        public DtlMerchantDetailsBundle createFromParcel(Parcel in) {
            return new DtlMerchantDetailsBundle(in);
        }

        @Override
        public DtlMerchantDetailsBundle[] newArray(int size) {
            return new DtlMerchantDetailsBundle[size];
        }
    };
}
