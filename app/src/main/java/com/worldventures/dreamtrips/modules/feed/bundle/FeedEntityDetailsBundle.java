package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedEntityDetailsBundle implements Parcelable {

    FeedItem feedItem;
    boolean slave;


    public FeedEntityDetailsBundle(FeedItem feedItem) {
        this(feedItem, false);
    }

    public FeedEntityDetailsBundle(FeedItem feedItem, boolean slave) {
        this.feedItem = feedItem;
        this.slave = slave;
    }


    public FeedItem getFeedItem() {
        return feedItem;
    }

    public void setFeedItem(FeedItem feedItem) {
        this.feedItem = feedItem;
    }

    public boolean isSlave() {
        return slave;
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.feedItem, 0);
        dest.writeByte(slave ? (byte) 1 : (byte) 0);
    }

    public FeedEntityDetailsBundle() {
    }

    protected FeedEntityDetailsBundle(Parcel in) {
        this.feedItem = in.readParcelable(FeedItem.class.getClassLoader());
        this.slave = in.readByte() != 0;
    }

    public static final Creator<FeedEntityDetailsBundle> CREATOR = new Creator<FeedEntityDetailsBundle>() {
        public FeedEntityDetailsBundle createFromParcel(Parcel source) {
            return new FeedEntityDetailsBundle(source);
        }

        public FeedEntityDetailsBundle[] newArray(int size) {
            return new FeedEntityDetailsBundle[size];
        }
    };
}
