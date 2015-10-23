package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedItemDetailsBundle extends CommentsBundle implements Parcelable {

    FeedItem feedItem;
    boolean slave;

    public FeedItemDetailsBundle(FeedItem feedItem) {
        this(feedItem, false);
    }

    public FeedItemDetailsBundle(FeedItem feedItem, boolean slave) {
        super(feedItem.getItem());
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
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.feedItem, 0);
        dest.writeByte(slave ? (byte) 1 : (byte) 0);
    }

    protected FeedItemDetailsBundle(Parcel in) {
        super(in);
        this.feedItem = in.readParcelable(FeedItem.class.getClassLoader());
        this.slave = in.readByte() != 0;
    }

    public static final Creator<FeedItemDetailsBundle> CREATOR = new Creator<FeedItemDetailsBundle>() {
        public FeedItemDetailsBundle createFromParcel(Parcel source) {
            return new FeedItemDetailsBundle(source);
        }

        public FeedItemDetailsBundle[] newArray(int size) {
            return new FeedItemDetailsBundle[size];
        }
    };
}
