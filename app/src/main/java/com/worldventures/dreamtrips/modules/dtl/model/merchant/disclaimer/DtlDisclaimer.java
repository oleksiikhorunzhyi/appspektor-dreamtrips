package com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlDisclaimer implements Parcelable {

    private Type type;
    private String text;

    public DtlDisclaimer() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum Type {
        POINTS, PERKS, ADDITIONAL
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlDisclaimer(Parcel in) {
        type = (Type) in.readSerializable();
        text = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(type);
        dest.writeString(text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DtlDisclaimer> CREATOR = new Creator<DtlDisclaimer>() {
        @Override
        public DtlDisclaimer createFromParcel(Parcel in) {
            return new DtlDisclaimer(in);
        }

        @Override
        public DtlDisclaimer[] newArray(int size) {
            return new DtlDisclaimer[size];
        }
    };

    @Override
    public String toString() {
        return text;
    }
}
