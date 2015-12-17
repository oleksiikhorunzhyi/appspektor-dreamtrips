package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PointsEstimationDialogBundle implements Parcelable {

    private String placeId;

    public PointsEstimationDialogBundle(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected PointsEstimationDialogBundle(Parcel in) {
        placeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
    }

    public static final Creator<PointsEstimationDialogBundle> CREATOR = new Creator<PointsEstimationDialogBundle>() {
        @Override
        public PointsEstimationDialogBundle createFromParcel(Parcel in) {
            return new PointsEstimationDialogBundle(in);
        }

        @Override
        public PointsEstimationDialogBundle[] newArray(int size) {
            return new PointsEstimationDialogBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
