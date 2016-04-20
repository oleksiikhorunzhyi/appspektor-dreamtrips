package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import rx.functions.Func2;

public class Position implements Parcelable, Serializable {

    private float x;
    private float y;

    public Position() {

    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    protected Position(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    public static final Func2<PhotoTag, PhotoTag, Integer> SORT_BY_POSITION = (o1, o2) -> {
        Position p1 = o1.getProportionalPosition().getTopLeft();
        Position p2 = o2.getProportionalPosition().getTopLeft();

        double hypot1 = Math.hypot(0 - p1.getX(), 0 - p1.getY());
        double hypot2 = Math.hypot(0 - p2.getX(), 0 - p2.getY());
        return hypot1 > hypot2 ? 1 : hypot1 == hypot2 ? 0 : -1;
    };

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (Float.compare(position.x, x) != 0) return false;
        return Float.compare(position.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}