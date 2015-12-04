package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class SuggestPlaceBundle implements Parcelable {

    private DtlMerchant place;

    public SuggestPlaceBundle(DtlMerchant place) {
        this.place = place;
    }

    public DtlMerchant getPlace() {
        return place;
    }

    public void setPlace(DtlMerchant place) {
        this.place = place;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected SuggestPlaceBundle(Parcel in) {
        place = in.readParcelable(DtlMerchant.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(place, flags);
    }

    public static final Creator<SuggestPlaceBundle> CREATOR = new Creator<SuggestPlaceBundle>() {
        @Override
        public SuggestPlaceBundle createFromParcel(Parcel in) {
            return new SuggestPlaceBundle(in);
        }

        @Override
        public SuggestPlaceBundle[] newArray(int size) {
            return new SuggestPlaceBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
