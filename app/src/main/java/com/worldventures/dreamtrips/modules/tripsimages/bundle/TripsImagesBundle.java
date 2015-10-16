package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class TripsImagesBundle implements Parcelable {

    private TripImagesListFragment.Type type;
    private int foreignUserId;

    public TripsImagesBundle(TripImagesListFragment.Type type) {
        this.type = type;
    }

    public TripsImagesBundle(TripImagesListFragment.Type type, int foreignUserId) {
        this.type = type;
        this.foreignUserId = foreignUserId;
    }

    protected TripsImagesBundle(Parcel in) {
        foreignUserId = in.readInt();
        type = (TripImagesListFragment.Type) in.readSerializable();
    }

    public static final Creator<TripsImagesBundle> CREATOR = new Creator<TripsImagesBundle>() {
        @Override
        public TripsImagesBundle createFromParcel(Parcel in) {
            return new TripsImagesBundle(in);
        }

        @Override
        public TripsImagesBundle[] newArray(int size) {
            return new TripsImagesBundle[size];
        }
    };

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public int getForeignUserId() {
        return foreignUserId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(foreignUserId);
        parcel.writeSerializable(type);
    }
}
