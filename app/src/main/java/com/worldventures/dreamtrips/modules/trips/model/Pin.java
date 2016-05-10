package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.Coordinates;

import java.util.List;

public class Pin extends MapObject {

    private boolean hasWelcomeTrips;
    private List<String> tripUids;

    public boolean isHasWelcomeTrips() {
        return hasWelcomeTrips;
    }

    public List<String> getTripUids() {
        return tripUids;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(hasWelcomeTrips ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.tripUids);
        dest.writeParcelable(this.coordinates, flags);
    }

    public Pin() {
    }

    protected Pin(Parcel in) {
        super(in);
        this.hasWelcomeTrips = in.readByte() != 0;
        this.tripUids = in.createStringArrayList();
        this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel source) {
            return new Pin(source);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };
}
