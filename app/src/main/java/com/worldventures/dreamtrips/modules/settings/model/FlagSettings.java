package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;

public class FlagSettings extends Settings<Boolean> {

    public FlagSettings() {

    }

    protected FlagSettings(Parcel in) {
        name = in.readString();
        type = (Type) in.readSerializable();
        value = (Boolean) in.readSerializable();
    }

    public FlagSettings(String name, Type type, boolean value) {
        super(name, type, value);
    }

    @Override
    public Boolean getValue() {
        return value == null ? false : value;
    }

    public static final Creator<FlagSettings> CREATOR = new Creator<FlagSettings>() {
        @Override
        public FlagSettings createFromParcel(Parcel in) {
            return new FlagSettings(in);
        }

        @Override
        public FlagSettings[] newArray(int size) {
            return new FlagSettings[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(type);
        dest.writeSerializable(value);
    }
}
