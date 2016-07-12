package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

public class UndefinedMapObjectHolder extends MapObjectHolder<MapObject> {

    public UndefinedMapObjectHolder() {
    }

    protected UndefinedMapObjectHolder(Parcel in) {
        super(in);
    }

    public static final Creator<UndefinedMapObjectHolder> CREATOR = new Creator<UndefinedMapObjectHolder>() {
        @Override
        public UndefinedMapObjectHolder createFromParcel(Parcel source) {
            return new UndefinedMapObjectHolder(source);
        }

        @Override
        public UndefinedMapObjectHolder[] newArray(int size) {
            return new UndefinedMapObjectHolder[size];
        }
    };
}
