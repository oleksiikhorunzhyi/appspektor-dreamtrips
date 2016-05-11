package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

public class TripMapListBundle implements Parcelable {

    private final List<TripModel> trips;

    public TripMapListBundle(List<TripModel> trips) {
        this.trips = trips;
    }

    public List<TripModel> getTrips() {
        return trips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.trips);
    }

    protected TripMapListBundle(Parcel in) {
        this.trips = new ArrayList<>();
        in.readList(this.trips, TripModel.class.getClassLoader());
    }

    public static final Creator<TripMapListBundle> CREATOR = new Creator<TripMapListBundle>() {
        @Override
        public TripMapListBundle createFromParcel(Parcel source) {
            return new TripMapListBundle(source);
        }

        @Override
        public TripMapListBundle[] newArray(int size) {
            return new TripMapListBundle[size];
        }
    };
}
