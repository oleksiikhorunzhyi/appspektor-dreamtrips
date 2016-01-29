package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Settings<T extends Serializable> implements Parcelable, Serializable {

    protected String name;
    protected Type type;
    protected T value;

    public Settings() {

    }

    public Settings(String name, Type type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    protected Settings(Parcel in) {
        name = in.readString();
        type = (Type) in.readSerializable();
        value = (T) in.readSerializable();
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(type);
        dest.writeSerializable(value);
    }

    public enum Type {
        @SerializedName("flag")
        FLAG,
        @SerializedName("select")
        SELECT,
        UNKNOWN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings<?> settings = (Settings<?>) o;

        return name.equals(settings.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
