package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.trips.model.TripMapDetailsAnchor;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

public class TripMapListBundle implements Parcelable {

    private final List<TripModel> trips;
    private final TripMapDetailsAnchor anchor;

    public TripMapListBundle(List<TripModel> trips, TripMapDetailsAnchor anchor) {
        this.trips = trips;
        this.anchor = anchor;
    }

    public List<TripModel> getTrips() {
        return trips;
    }

    public TripMapDetailsAnchor getAnchor() {
        return anchor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.trips);
        dest.writeParcelable(this.anchor, flags);
    }

    protected TripMapListBundle(Parcel in) {
        this.trips = new ArrayList<>();
        in.readList(this.trips, TripModel.class.getClassLoader());
        this.anchor = in.readParcelable(TripMapDetailsAnchor.class.getClassLoader());
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
