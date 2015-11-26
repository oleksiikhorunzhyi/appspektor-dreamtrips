package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

/**
 * Bundle to be supplied for {@link com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment}
 */
public class PlacesListBundle implements Parcelable {

    private DtlPlaceType placeType;

    public PlacesListBundle(DtlPlaceType placeType) {
        this.placeType = placeType;
    }

    public DtlPlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(DtlPlaceType placeType) {
        this.placeType = placeType;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected PlacesListBundle(Parcel in) {
        placeType = (DtlPlaceType) in.readSerializable();
    }

    public static final Creator<PlacesListBundle> CREATOR = new Creator<PlacesListBundle>() {
        @Override
        public PlacesListBundle createFromParcel(Parcel in) {
            return new PlacesListBundle(in);
        }

        @Override
        public PlacesListBundle[] newArray(int size) {
            return new PlacesListBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(placeType);
    }
}
