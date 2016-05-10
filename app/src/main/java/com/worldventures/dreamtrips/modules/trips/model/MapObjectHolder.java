package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MapObjectHolder<T extends MapObject> implements Parcelable {

    protected Type type;
    protected T item;

    public Type getType() {
        return type;
    }

    public T getItem() {
        return item;
    }

    public enum Type {

        @SerializedName("pin")
        PIN,
        @SerializedName("cluster")
        CLUSTER,
        UNDEFINED
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeSerializable(this.item);
    }

    public MapObjectHolder() {
    }

    protected MapObjectHolder(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.item = (T) in.readSerializable();
    }

}
