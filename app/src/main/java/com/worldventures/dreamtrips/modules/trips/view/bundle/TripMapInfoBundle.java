package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class TripMapInfoBundle implements Parcelable {

    public final TripModel tripModel;

    public TripMapInfoBundle(TripModel tripModel) {
        this.tripModel = tripModel;
    }

    public TripMapInfoBundle(Parcel in) {
        tripModel = (TripModel) in.readSerializable();
    }

    public static final Creator<TripMapInfoBundle> CREATOR = new Creator<TripMapInfoBundle>() {
        @Override
        public TripMapInfoBundle createFromParcel(Parcel in) {
            return new TripMapInfoBundle(in);
        }

        @Override
        public TripMapInfoBundle[] newArray(int size) {
            return new TripMapInfoBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(tripModel);
    }
}
