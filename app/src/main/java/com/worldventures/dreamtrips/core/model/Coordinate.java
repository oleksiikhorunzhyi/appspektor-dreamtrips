package com.worldventures.dreamtrips.core.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinate extends BaseEntity implements Parcelable {
    double latitude;
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public Coordinate() {
    }

    private Coordinate(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        public Coordinate createFromParcel(Parcel source) {
            return new Coordinate(source);
        }

        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}
