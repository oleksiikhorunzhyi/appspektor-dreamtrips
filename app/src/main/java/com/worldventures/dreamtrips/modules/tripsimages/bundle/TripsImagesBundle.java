package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class TripsImagesBundle implements Parcelable {

    private Type type;
    private int userId;

    public TripsImagesBundle(Type type, int userId) {
        this.type = type;
        this.userId = userId;
    }

    protected TripsImagesBundle(Parcel in) {
        userId = in.readInt();
        type = (Type) in.readSerializable();
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

    public Type getType() {
        return type;
    }

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
        parcel.writeSerializable(type);
    }
}
