package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

public class FullScreenImagesBundle implements Parcelable {

    private TripImagesListFragment.Type tab;
    private int userId;
    private int position;
    private ArrayList<IFullScreenObject> fixedList;
    private boolean foreign;
    private Route route;

    public FullScreenImagesBundle() {
    }

    protected FullScreenImagesBundle(Parcel in) {
        tab = (TripImagesListFragment.Type) in.readSerializable();
        userId = in.readInt();
        position = in.readInt();
        fixedList = (ArrayList<IFullScreenObject>) in.readSerializable();
        foreign = in.readByte() == 1;
        route = (Route) in.readSerializable();
    }

    public TripImagesListFragment.Type getTab() {
        return tab;
    }

    public int getUserId() {
        return userId;
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

    public Route getRoute() {
        return route;
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
        parcel.writeSerializable(tab);
        parcel.writeInt(userId);
        parcel.writeInt(position);
        parcel.writeSerializable(fixedList);
        parcel.writeByte((byte) (foreign ? 1 : 0));
        parcel.writeSerializable(route);
    }

    public static class Builder {

        private FullScreenImagesBundle instance;

        public Builder() {
            instance = new FullScreenImagesBundle();
        }

        public Builder type(TripImagesListFragment.Type type) {
            instance.tab = type;
            return this;
        }

        public Builder userId(int userId) {
            instance.userId = userId;
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

        public Builder route(Route route) {
            instance.route = route;
            return this;
        }

        public FullScreenImagesBundle build() {
            return instance;
        }
    }
}
