package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Hashtag implements Parcelable, Serializable {
    @SerializedName("name")
    String hashtag;

    public Hashtag() {
    }

    public Hashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    protected Hashtag(Parcel in) {
        hashtag = in.readString();
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public static final Creator<Hashtag> CREATOR = new Creator<Hashtag>() {
        @Override
        public Hashtag createFromParcel(Parcel in) {
            return new Hashtag(in);
        }

        @Override
        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hashtag hashtag1 = (Hashtag) o;

        return hashtag != null ? hashtag.equals(hashtag1.hashtag) : hashtag1.hashtag == null;
    }

    @Override
    public int hashCode() {
        return hashtag != null ? hashtag.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "hashtag='" + hashtag + '\'' +
                '}';
    }
}
