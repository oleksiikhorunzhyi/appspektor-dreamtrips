package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class FullScreenPhotoBundle implements Parcelable {

    private IFullScreenObject photo;
    private TripImagesListFragment.Type type;
    private boolean foreign;

    public FullScreenPhotoBundle(IFullScreenObject photo, TripImagesListFragment.Type type, boolean foreign) {
        this.photo = photo;
        this.type = type;
        this.foreign = foreign;
    }

    protected FullScreenPhotoBundle(Parcel in) {
        photo = in.readParcelable(IFullScreenObject.class.getClassLoader());
        type = (TripImagesListFragment.Type) in.readSerializable();
        foreign = in.readByte() == 1;
    }

    public IFullScreenObject getPhoto() {
        return photo;
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public boolean isForeign() {
        return foreign;
    }

    public static final Creator<FullScreenPhotoBundle> CREATOR = new Creator<FullScreenPhotoBundle>() {
        @Override
        public FullScreenPhotoBundle createFromParcel(Parcel in) {
            return new FullScreenPhotoBundle(in);
        }

        @Override
        public FullScreenPhotoBundle[] newArray(int size) {
            return new FullScreenPhotoBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(photo, i);
        parcel.writeSerializable(type);
        parcel.writeByte((byte) (foreign ? 1 : 0));
    }
}
