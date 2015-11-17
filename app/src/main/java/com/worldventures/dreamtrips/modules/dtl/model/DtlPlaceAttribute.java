package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class DtlPlaceAttribute implements Parcelable {

    private String name;
    private List<String> attributes;

    public DtlPlaceAttribute() {
    }

    public String getName() {
        return name;
    }

    public List<DtlPlacesFilterAttribute> getAttributes() {
        return Queryable.from(attributes).map(DtlPlacesFilterAttribute::new).toList();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlaceAttribute(Parcel in) {
        name = in.readString();
        attributes = in.createStringArrayList();
    }

    public static final Creator<DtlPlaceAttribute> CREATOR = new Creator<DtlPlaceAttribute>() {
        @Override
        public DtlPlaceAttribute createFromParcel(Parcel in) {
            return new DtlPlaceAttribute(in);
        }

        @Override
        public DtlPlaceAttribute[] newArray(int size) {
            return new DtlPlaceAttribute[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(attributes);
    }
}
