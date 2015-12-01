package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlaceDetailsFragment;

/**
 * Bundle to be supplied when navigating to {@link DtlPlaceDetailsFragment}
 */
public class PlaceDetailsBundle implements Parcelable {

    private DTlMerchant place;
    /**
     * Set to false when showing details as separate activity, true when showing as a fragment in current screen hierarchy
     */
    private boolean isSlave;

    /**
     * @param place place model to supply to screen
     * @param isSlave indicator that screen is slave (shown in current view hierarchy) or master (new activity)
     */
    public PlaceDetailsBundle(DTlMerchant place, boolean isSlave) {
        this.place = place;
        this.isSlave = isSlave;
    }

    public DTlMerchant getPlace() {
        return place;
    }

    public boolean isSlave() {
        return isSlave;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected PlaceDetailsBundle(Parcel in) {
        place = in.readParcelable(DTlMerchant.class.getClassLoader());
        isSlave = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(place, flags);
        dest.writeByte((byte) (isSlave ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceDetailsBundle> CREATOR = new Creator<PlaceDetailsBundle>() {
        @Override
        public PlaceDetailsBundle createFromParcel(Parcel in) {
            return new PlaceDetailsBundle(in);
        }

        @Override
        public PlaceDetailsBundle[] newArray(int size) {
            return new PlaceDetailsBundle[size];
        }
    };
}
