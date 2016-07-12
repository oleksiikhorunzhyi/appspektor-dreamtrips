package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

public class ClusterHolder extends MapObjectHolder<Cluster> {

    public ClusterHolder() {
    }

    public ClusterHolder(Parcel in) {
        super(in);
    }

    public static final Creator<ClusterHolder> CREATOR = new Creator<ClusterHolder>() {
        @Override
        public ClusterHolder createFromParcel(Parcel source) {
            return new ClusterHolder(source);
        }

        @Override
        public ClusterHolder[] newArray(int size) {
            return new ClusterHolder[size];
        }
    };
}
