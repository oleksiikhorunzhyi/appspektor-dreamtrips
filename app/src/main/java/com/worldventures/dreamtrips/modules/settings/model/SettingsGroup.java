package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingsGroup implements Parcelable {

    private String title;

    protected SettingsGroup(Parcel in) {
        title = in.readString();
    }

    public static final Creator<SettingsGroup> CREATOR = new Creator<SettingsGroup>() {
        @Override
        public SettingsGroup createFromParcel(Parcel in) {
            return new SettingsGroup(in);
        }

        @Override
        public SettingsGroup[] newArray(int size) {
            return new SettingsGroup[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }
}
