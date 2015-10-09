package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

/**
 * Bundle to be supplied when navigating to {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesTabsFragment}
 */
public class PlacesBundle implements Parcelable {

    private DtlLocation location;

    public PlacesBundle(DtlLocation location) {
        this.location = location;
    }

    public DtlLocation getLocation() {
        return location;
    }

    public void setLocation(DtlLocation location) {
        this.location = location;
    }

    protected PlacesBundle(Parcel in) {
        location = in.readParcelable(DtlLocation.class.getClassLoader());
    }

    public static final Creator<PlacesBundle> CREATOR = new Creator<PlacesBundle>() {
        @Override
        public PlacesBundle createFromParcel(Parcel in) {
            return new PlacesBundle(in);
        }

        @Override
        public PlacesBundle[] newArray(int size) {
            return new PlacesBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
    }
}
