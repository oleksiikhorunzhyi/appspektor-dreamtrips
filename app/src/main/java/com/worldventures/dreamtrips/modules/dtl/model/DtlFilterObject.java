package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlFilterObject implements Parcelable {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;

    private int minPrice;
    private int maxPrice;

    private int maxDistance;

    public DtlFilterObject() {
        reset();
    }

    protected DtlFilterObject(Parcel in) {
        minPrice = in.readInt();
        maxPrice = in.readInt();
        maxDistance = in.readInt();
    }

    public static final Creator<DtlFilterObject> CREATOR = new Creator<DtlFilterObject>() {
        @Override
        public DtlFilterObject createFromParcel(Parcel in) {
            return new DtlFilterObject(in);
        }

        @Override
        public DtlFilterObject[] newArray(int size) {
            return new DtlFilterObject[size];
        }
    };

    public void reset() {
        minPrice = MIN_PRICE;
        maxPrice = MAX_PRICE;
        maxDistance = MAX_DISTANCE;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setPrice(int minPrice, int maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeInt(maxDistance);
    }
}
