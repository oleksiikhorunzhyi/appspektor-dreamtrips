package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.Serializable;
import java.util.ArrayList;

public class FullScreenImagesBundle implements Parcelable {

    private TripImagesListFragment.Type type;
    private int foreignUserId;
    private int position;
    private ArrayList<IFullScreenObject> fixedList;
    private boolean foreign;

    public FullScreenImagesBundle() {
    }

    protected FullScreenImagesBundle(Parcel in) {
        type = (TripImagesListFragment.Type) in.readSerializable();
        foreignUserId = in.readInt();
        position = in.readInt();
        fixedList = (ArrayList<IFullScreenObject>) in.readSerializable();
        foreign = in.readByte() == 1;
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public int getForeignUserId() {
        return foreignUserId;
    }

    public int getPosition() {
        return position;
    }

    public ArrayList<IFullScreenObject> getFixedList() {
        return fixedList;
    }

    public boolean isForeign() {
        return foreign;
    }

    public static final Creator<FullScreenImagesBundle> CREATOR = new Creator<FullScreenImagesBundle>() {
        @Override
        public FullScreenImagesBundle createFromParcel(Parcel in) {
            return new FullScreenImagesBundle(in);
        }

        @Override
        public FullScreenImagesBundle[] newArray(int size) {
            return new FullScreenImagesBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(type);
        parcel.writeInt(foreignUserId);
        parcel.writeInt(position);
        parcel.writeSerializable(fixedList);
        parcel.writeByte((byte) (foreign ? 1 : 0));
    }

    public static class Builder {

        private FullScreenImagesBundle instance;

        public Builder() {
            instance = new FullScreenImagesBundle();
        }

        public Builder type(TripImagesListFragment.Type type) {
            instance.type = type;
            return this;
        }

        public Builder foreignUserId(int userId) {
            instance.foreignUserId = userId;
            return this;
        }

        public Builder position(int position) {
            instance.position = position;
            return this;
        }

        public Builder fixedList(ArrayList<IFullScreenObject> list) {
            instance.fixedList = list;
            return this;
        }

        public Builder foreign(boolean foreign) {
            instance.foreign = foreign;
            return this;
        }

        public FullScreenImagesBundle build() {
            return instance;
        }
    }
}
