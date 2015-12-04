package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

/**
 * Bundle to be supplied for {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment}
 */
public class PlacesListBundle implements Parcelable {

    private DtlMerchantType placeType;

    public PlacesListBundle(DtlMerchantType placeType) {
        this.placeType = placeType;
    }

    public DtlMerchantType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(DtlMerchantType placeType) {
        this.placeType = placeType;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected PlacesListBundle(Parcel in) {
        placeType = (DtlMerchantType) in.readSerializable();
    }

    public static final Creator<PlacesListBundle> CREATOR = new Creator<PlacesListBundle>() {
        @Override
        public PlacesListBundle createFromParcel(Parcel in) {
            return new PlacesListBundle(in);
        }

        @Override
        public PlacesListBundle[] newArray(int size) {
            return new PlacesListBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(placeType);
    }
}
