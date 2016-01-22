package com.worldventures.dreamtrips.modules.settings.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

public class SettingDetailsBundle implements Parcelable {

    public final SettingsGroup settingsGroup;

    public SettingDetailsBundle(SettingsGroup settingsGroup) {
        this.settingsGroup = settingsGroup;
    }

    protected SettingDetailsBundle(Parcel in) {
        settingsGroup = in.readParcelable(SettingsGroup.class.getClassLoader());
    }

    public static final Creator<SettingDetailsBundle> CREATOR = new Creator<SettingDetailsBundle>() {
        @Override
        public SettingDetailsBundle createFromParcel(Parcel in) {
            return new SettingDetailsBundle(in);
        }

        @Override
        public SettingDetailsBundle[] newArray(int size) {
            return new SettingDetailsBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(settingsGroup, flags);
    }
}
