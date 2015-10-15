package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class SuggestMerchantBundle implements Parcelable {

    private DtlPlace place;

    public SuggestMerchantBundle(DtlPlace place) {
        this.place = place;
    }

    public DtlPlace getPlace() {
        return place;
    }

    public void setPlace(DtlPlace place) {
        this.place = place;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected SuggestMerchantBundle(Parcel in) {
        place = in.readParcelable(DtlPlace.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(place, flags);
    }

    public static final Creator<SuggestMerchantBundle> CREATOR = new Creator<SuggestMerchantBundle>() {
        @Override
        public SuggestMerchantBundle createFromParcel(Parcel in) {
            return new SuggestMerchantBundle(in);
        }

        @Override
        public SuggestMerchantBundle[] newArray(int size) {
            return new SuggestMerchantBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
