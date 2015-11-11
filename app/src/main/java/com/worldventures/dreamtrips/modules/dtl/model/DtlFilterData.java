package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public class DtlFilterData implements Parcelable {

    public static final int MIN_PRICE = 1;
    public static final int MAX_PRICE = 5;
    public static final int MAX_DISTANCE = 50;

    private int minPrice;
    private int maxPrice;

    private int maxDistance;
    private boolean distanceEnabled;

    private Distance distance;

    public DtlFilterData() {
        reset();
    }

    protected DtlFilterData(Parcel in) {
        minPrice = in.readInt();
        maxPrice = in.readInt();
        maxDistance = in.readInt();
        distanceEnabled = in.readInt() != 0;
        distance = (Distance) in.readSerializable();
    }

    public static final Creator<DtlFilterData> CREATOR = new Creator<DtlFilterData>() {
        @Override
        public DtlFilterData createFromParcel(Parcel in) {
            return new DtlFilterData(in);
        }

        @Override
        public DtlFilterData[] newArray(int size) {
            return new DtlFilterData[size];
        }
    };

    public void reset() {
        minPrice = MIN_PRICE;
        maxPrice = MAX_PRICE;
        maxDistance = MAX_DISTANCE;
        distance = Distance.MILES;
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

    public boolean isDistanceEnabled() {
        return distanceEnabled;
    }

    public void setDistanceEnabled(boolean distanceEnabled) {
        this.distanceEnabled = distanceEnabled;
    }

    public Distance getDistance() {
        return distance;
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
        dest.writeInt(distanceEnabled ? 1 : 0);
        dest.writeSerializable(distance);
    }

    public void toggleDistance() {
        if (distance == Distance.KMS) distance = Distance.MILES;
        else distance = Distance.KMS;
    }


    public enum Distance {
        MILES(R.string.miles, true), KMS(R.string.kms, false);

        @StringRes
        int textResId;
        boolean selected;

        Distance(@StringRes int textResId, boolean selected) {
            this.textResId = textResId;
            this.selected = selected;
        }

        public int getTextResId() {
            return textResId;
        }

        public boolean isSelected() {
            return selected;
        }
    }
}
