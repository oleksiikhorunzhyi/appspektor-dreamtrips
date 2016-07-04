package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedHashtagBundle implements Parcelable{
    String hashtag;

    public FeedHashtagBundle(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getHashtag() {
        return hashtag;
    }

    protected FeedHashtagBundle(Parcel in) {
        hashtag = in.readString();
    }

    public static final Creator<FeedHashtagBundle> CREATOR = new Creator<FeedHashtagBundle>() {
        @Override
        public FeedHashtagBundle createFromParcel(Parcel in) {
            return new FeedHashtagBundle(in);
        }

        @Override
        public FeedHashtagBundle[] newArray(int size) {
            return new FeedHashtagBundle[size];
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
}
