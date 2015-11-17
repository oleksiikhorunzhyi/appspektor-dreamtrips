package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class DtlPlaceAttributeSet implements Parcelable {

    private String name;
    private List<DtlPlaceAttribute> attributes;

    public DtlPlaceAttributeSet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttributes(List<DtlPlaceAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<DtlPlaceAttribute> getAttributes() {
        return attributes;
    }

    public List<DtlPlacesFilterAttribute> getFilterAttributes() {
        return Queryable.from(attributes).map(attribute -> new DtlPlacesFilterAttribute(attribute.getName())).toList();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlaceAttributeSet(Parcel in) {
        name = in.readString();
        attributes = in.createTypedArrayList(DtlPlaceAttribute.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(attributes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DtlPlaceAttributeSet> CREATOR = new Creator<DtlPlaceAttributeSet>() {
        @Override
        public DtlPlaceAttributeSet createFromParcel(Parcel in) {
            return new DtlPlaceAttributeSet(in);
        }

        @Override
        public DtlPlaceAttributeSet[] newArray(int size) {
            return new DtlPlaceAttributeSet[size];
        }
    };
}
