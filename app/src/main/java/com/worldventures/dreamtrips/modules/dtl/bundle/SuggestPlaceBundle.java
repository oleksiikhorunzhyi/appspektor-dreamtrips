package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

public class SuggestPlaceBundle implements Parcelable {

    private DtlPlace place;

    public SuggestPlaceBundle(DtlPlace place) {
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

    protected SuggestPlaceBundle(Parcel in) {
        place = in.readParcelable(DtlPlace.class.getClassLoader());
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
