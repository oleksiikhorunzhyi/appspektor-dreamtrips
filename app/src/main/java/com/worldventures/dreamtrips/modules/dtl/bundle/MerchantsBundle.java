package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsTabsFragment;

/**
 * Bundle to be supplied when navigating to {@link DtlMerchantsTabsFragment}
 */
public class MerchantsBundle implements Parcelable {

    private DtlLocation location;

    public MerchantsBundle(DtlLocation location) {
        this.location = location;
    }

    public DtlLocation getLocation() {
        return location;
    }

    public void setLocation(DtlLocation location) {
        this.location = location;
    }

    protected MerchantsBundle(Parcel in) {
        location = in.readParcelable(DtlLocation.class.getClassLoader());
    }

    public static final Creator<MerchantsBundle> CREATOR = new Creator<MerchantsBundle>() {
        @Override
        public MerchantsBundle createFromParcel(Parcel in) {
            return new MerchantsBundle(in);
        }

        @Override
        public MerchantsBundle[] newArray(int size) {
            return new MerchantsBundle[size];
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
