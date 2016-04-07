package com.worldventures.dreamtrips.modules.common.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PickerBundle implements Parcelable {

    private boolean multipickEnabled;
    private int pickLimit;
    private int requestId;

    public PickerBundle(int requestId) {
        this(requestId, 1);
    }

    public PickerBundle(int requestId, int pickLimit) {
        this(requestId, pickLimit, pickLimit > 1);
    }

    public PickerBundle(int requestId, int pickLimit, boolean multipickEnabled) {
        this.requestId = requestId;
        this.pickLimit = pickLimit;
        this.multipickEnabled = multipickEnabled;
    }

    public boolean isMultipickEnabled() {
        return multipickEnabled;
    }

    public int getPickLimit() {
        return pickLimit;
    }

    public int getRequestId() {
        return requestId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(multipickEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pickLimit);
        dest.writeInt(this.requestId);
    }

    protected PickerBundle(Parcel in) {
        this.multipickEnabled = in.readByte() != 0;
        this.pickLimit = in.readInt();
        this.requestId = in.readInt();
    }

    public static final Creator<PickerBundle> CREATOR = new Creator<PickerBundle>() {
        @Override
        public PickerBundle createFromParcel(Parcel source) {
            return new PickerBundle(source);
        }

        @Override
        public PickerBundle[] newArray(int size) {
            return new PickerBundle[size];
        }
    };
}
