package com.worldventures.dreamtrips.core.component;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

public class ComponentDescription implements Parcelable {
    private final String key;
    private final int title;
    private final int icon;
    private final boolean ignored;
    private final Class<? extends Fragment> fragmentClass;

    public ComponentDescription(String key, int titleRes, int iconRes, Class<? extends Fragment> fragmentClass) {
        this(key, titleRes, iconRes, false, fragmentClass);
    }

    public ComponentDescription(String key, int title, int icon, boolean ignored, Class<? extends Fragment> fragmentClass) {
        this.key = key;
        this.title = title;
        this.icon = icon;
        this.ignored = ignored;
        this.fragmentClass = fragmentClass;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public String getKey() {
        return key;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentDescription that = (ComponentDescription) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeInt(this.title);
        dest.writeInt(this.icon);
        dest.writeByte(ignored ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.fragmentClass);
    }

    private ComponentDescription(Parcel in) {
        this.key = in.readString();
        this.title = in.readInt();
        this.icon = in.readInt();
        this.ignored = in.readByte() != 0;
        this.fragmentClass = (Class<? extends Fragment>) in.readSerializable();
    }

    public static final Creator<ComponentDescription> CREATOR = new Creator<ComponentDescription>() {
        public ComponentDescription createFromParcel(Parcel source) {
            return new ComponentDescription(source);
        }

        public ComponentDescription[] newArray(int size) {
            return new ComponentDescription[size];
        }
    };
}
