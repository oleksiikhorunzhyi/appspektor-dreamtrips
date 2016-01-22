package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

public class SelectSettings extends Settings<String> {

    private List<String> options;

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public SelectSettings() {

    }

    public SelectSettings(String name, Type type, String value, List<String> options) {
        super(name, type, value);
        this.options = options;
    }

    protected SelectSettings(Parcel in) {
        id = in.readInt();
        name = in.readString();
        type = (Type) in.readSerializable();
        value = (String) in.readSerializable();
        options = new ArrayList<>();
        in.readStringList(options);
    }

    public static final Creator<SelectSettings> CREATOR = new Creator<SelectSettings>() {
        @Override
        public SelectSettings createFromParcel(Parcel in) {
            return new SelectSettings(in);
        }

        @Override
        public SelectSettings[] newArray(int size) {
            return new SelectSettings[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeSerializable(type);
        dest.writeSerializable(value);
        dest.writeStringList(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SelectSettings that = (SelectSettings) o;

        return options != null && that.options != null && options.containsAll(that.options)
                && that.options.containsAll(options);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }
}
