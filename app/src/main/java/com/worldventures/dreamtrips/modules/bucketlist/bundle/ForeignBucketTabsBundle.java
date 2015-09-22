package com.worldventures.dreamtrips.modules.bucketlist.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class ForeignBucketTabsBundle implements Parcelable {

    private int userId;

    public ForeignBucketTabsBundle(int userId) {
        this.userId = userId;
    }

    protected ForeignBucketTabsBundle(Parcel in) {
        userId = in.readInt();
    }

    public static final Creator<ForeignBucketTabsBundle> CREATOR = new Creator<ForeignBucketTabsBundle>() {
        @Override
        public ForeignBucketTabsBundle createFromParcel(Parcel in) {
            return new ForeignBucketTabsBundle(in);
        }

        @Override
        public ForeignBucketTabsBundle[] newArray(int size) {
            return new ForeignBucketTabsBundle[size];
        }
    };

    public int getUserId() {
        return userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
    }
}
