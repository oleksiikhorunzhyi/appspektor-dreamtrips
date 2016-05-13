package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TripMapDetailsAnchor implements Parcelable {

    private Position pointerPosition;
    private int margin;

    public TripMapDetailsAnchor(Position pointerPosition) {
        this.pointerPosition = pointerPosition;
    }

    public enum Position {
        LEFT, RIGHT, BOTTOM
    }

    public Position getPointerPosition() {
        return pointerPosition;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pointerPosition == null ? -1 : this.pointerPosition.ordinal());
        dest.writeInt(this.margin);
    }

    protected TripMapDetailsAnchor(Parcel in) {
        int tmpPointerPosition = in.readInt();
        this.pointerPosition = tmpPointerPosition == -1 ? null : Position.values()[tmpPointerPosition];
        this.margin = in.readInt();
    }

    public static final Creator<TripMapDetailsAnchor> CREATOR = new Creator<TripMapDetailsAnchor>() {
        @Override
        public TripMapDetailsAnchor createFromParcel(Parcel source) {
            return new TripMapDetailsAnchor(source);
        }

        @Override
        public TripMapDetailsAnchor[] newArray(int size) {
            return new TripMapDetailsAnchor[size];
        }
    };
}
