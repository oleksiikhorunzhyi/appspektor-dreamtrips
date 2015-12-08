package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantDetailsFragment;

/**
 * Bundle to be supplied when navigating to {@link DtlMerchantDetailsFragment}
 */
public class MerchantDetailsBundle implements Parcelable {

    private DtlMerchant dtlMerchant;
    /**
     * Set to false when showing details as separate activity, true when showing as a fragment in current screen hierarchy
     */
    private boolean isSlave;

    /**
     * @param dtlMerchant dtlMerchant model to supply to screen
     * @param isSlave indicator that screen is slave (shown in current view hierarchy) or master (new activity)
     */
    public MerchantDetailsBundle(DtlMerchant dtlMerchant, boolean isSlave) {
        this.dtlMerchant = dtlMerchant;
        this.isSlave = isSlave;
    }

    public DtlMerchant getDtlMerchant() {
        return dtlMerchant;
    }

    public boolean isSlave() {
        return isSlave;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected MerchantDetailsBundle(Parcel in) {
        dtlMerchant = in.readParcelable(DtlMerchant.class.getClassLoader());
        isSlave = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(dtlMerchant, flags);
        dest.writeByte((byte) (isSlave ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MerchantDetailsBundle> CREATOR = new Creator<MerchantDetailsBundle>() {
        @Override
        public MerchantDetailsBundle createFromParcel(Parcel in) {
            return new MerchantDetailsBundle(in);
        }

        @Override
        public MerchantDetailsBundle[] newArray(int size) {
            return new MerchantDetailsBundle[size];
        }
    };
}
