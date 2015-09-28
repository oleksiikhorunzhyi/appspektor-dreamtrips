package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class CommentsBundle implements Parcelable {
    FeedItem feedItem;
    boolean openKeyboard;

    public CommentsBundle(FeedItem feedItem) {
        this(feedItem, false);
    }

    public CommentsBundle(FeedItem feedItem, boolean openKeyboard) {
        this.feedItem = feedItem;
        this.openKeyboard = openKeyboard;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

    public boolean isOpenKeyboard() {
        return openKeyboard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.feedItem, 0);
        dest.writeByte(openKeyboard ? (byte) 1 : (byte) 0);
    }

    protected CommentsBundle(Parcel in) {
        this.feedItem = in.readParcelable(FeedItem.class.getClassLoader());
        this.openKeyboard = in.readByte() != 0;
    }

    public static final Creator<CommentsBundle> CREATOR = new Creator<CommentsBundle>() {
        public CommentsBundle createFromParcel(Parcel source) {
            return new CommentsBundle(source);
        }

        public CommentsBundle[] newArray(int size) {
            return new CommentsBundle[size];
        }
    };
}
