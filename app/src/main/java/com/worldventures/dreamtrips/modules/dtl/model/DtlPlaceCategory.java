package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DtlPlaceCategory implements Parcelable {

    private int id;
    private String name;

    public DtlPlaceCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlaceCategory(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<DtlPlaceCategory> CREATOR = new Creator<DtlPlaceCategory>() {
        @Override
        public DtlPlaceCategory createFromParcel(Parcel in) {
            return new DtlPlaceCategory(in);
        }

        @Override
        public DtlPlaceCategory[] newArray(int size) {
            return new DtlPlaceCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
