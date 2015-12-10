package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class FullScreenPhotoBundle implements Parcelable {

    private IFullScreenObject photo;
    private Type tab;
    private boolean foreign;

    public FullScreenPhotoBundle(IFullScreenObject photo, Type tab, boolean foreign) {
        this.photo = photo;
        this.tab = tab;
        this.foreign = foreign;
    }

    public IFullScreenObject getPhoto() {
        return photo;
    }

    public Type getTab() {
        return tab;
    }

    public boolean isForeign() {
        return foreign;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.photo, 0);
        dest.writeInt(this.tab == null ? -1 : this.tab.ordinal());
        dest.writeByte(foreign ? (byte) 1 : (byte) 0);
    }

    protected FullScreenPhotoBundle(Parcel in) {
        this.photo = in.readParcelable(IFullScreenObject.class.getClassLoader());
        int tmpTab = in.readInt();
        this.tab = tmpTab == -1 ? null : Type.values()[tmpTab];
        this.foreign = in.readByte() != 0;
    }

    public static final Creator<FullScreenPhotoBundle> CREATOR = new Creator<FullScreenPhotoBundle>() {
        public FullScreenPhotoBundle createFromParcel(Parcel source) {
            return new FullScreenPhotoBundle(source);
        }

        public FullScreenPhotoBundle[] newArray(int size) {
            return new FullScreenPhotoBundle[size];
        }
    };
}
