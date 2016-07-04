package com.worldventures.dreamtrips.modules.feed.model.feed.hashtag;

import android.os.Parcel;
import android.os.Parcelable;

public class HashtagSuggestion implements Parcelable{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public HashtagSuggestion() {
    }

    protected HashtagSuggestion(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<HashtagSuggestion> CREATOR = new Creator<HashtagSuggestion>() {
        @Override
        public HashtagSuggestion createFromParcel(Parcel source) {
            return new HashtagSuggestion(source);
        }

        @Override
        public HashtagSuggestion[] newArray(int size) {
            return new HashtagSuggestion[size];
        }
    };
}
