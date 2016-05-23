package com.worldventures.dreamtrips.modules.dtl_flow;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ViewState implements Parcelable {

    public ViewState() {
    }

    protected ViewState(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final class EMPTY extends ViewState {

        ///////////////////////////////////////////////////////////////////////////
        // Parcelable
        ///////////////////////////////////////////////////////////////////////////

        public static final Parcelable.Creator<EMPTY> CREATOR = new Parcelable.Creator<EMPTY>() {
            public EMPTY createFromParcel(Parcel source) {
                return new EMPTY(source);
            }

            public EMPTY[] newArray(int size) {
                return new EMPTY[size];
            }
        };

        public EMPTY(Parcel in) {
            super(in);
        }
    }
}
