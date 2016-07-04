package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class DescriptionBundle implements Parcelable {

    private String text;

    public DescriptionBundle(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
    }

    public DescriptionBundle() {
    }

    protected DescriptionBundle(Parcel in) {
        this.text = in.readString();
    }

    public static final Parcelable.Creator<DescriptionBundle> CREATOR = new Parcelable.Creator<DescriptionBundle>() {
        @Override
        public DescriptionBundle createFromParcel(Parcel source) {
            return new DescriptionBundle(source);
        }

        @Override
        public DescriptionBundle[] newArray(int size) {
            return new DescriptionBundle[size];
        }
    };
}
