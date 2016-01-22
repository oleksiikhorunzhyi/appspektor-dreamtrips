package com.worldventures.dreamtrips.modules.settings.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.settings.model.Settings;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SettingDetailsBundle implements Parcelable, Serializable {

    public final SettingsGroup settingsGroup;
    public final List<Settings> settingsList;

    public SettingDetailsBundle(SettingsGroup settingsGroup, List<Settings> settingsList) {
        this.settingsGroup = settingsGroup;
        this.settingsList = settingsList;
    }

    protected SettingDetailsBundle(Parcel in) {
        settingsGroup = in.readParcelable(SettingsGroup.class.getClassLoader());
        settingsList = new ArrayList<>();
        in.readList(settingsList, Settings.class.getClassLoader());
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
        dest.writeList(settingsList);
    }
}
