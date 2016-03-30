package com.worldventures.dreamtrips.modules.dtl_flow.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ViewState implements Parcelable {

    protected ViewState(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
