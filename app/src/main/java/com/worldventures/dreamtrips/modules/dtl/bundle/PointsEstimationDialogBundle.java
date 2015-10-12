package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PointsEstimationDialogBundle implements Parcelable {

    private int placeId;

    public PointsEstimationDialogBundle(int placeId) {
        this.placeId = placeId;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected PointsEstimationDialogBundle(Parcel in) {
        placeId = in.readInt();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(placeId);
    }
}
