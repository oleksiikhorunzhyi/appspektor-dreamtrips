package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedDetailsBundle extends CommentsBundle implements Parcelable {

    public static final Creator<FeedDetailsBundle> CREATOR = new Creator<FeedDetailsBundle>() {
        public FeedDetailsBundle createFromParcel(Parcel source) {
            return new FeedDetailsBundle(source);
        }

        public FeedDetailsBundle[] newArray(int size) {
            return new FeedDetailsBundle[size];
        }
    };

    FeedItem feedItem;
    boolean slave;
    boolean showAdditionalInfo;

    public FeedDetailsBundle(FeedItem feedItem) {
        this(feedItem, false);
    }

    public FeedDetailsBundle(FeedItem feedItem, boolean slave) {
        this(feedItem, slave, true);
    }

    public FeedDetailsBundle(FeedItem feedItem, boolean slave, boolean showAdditionalInfo) {
        super(feedItem.getItem());
        this.feedItem = feedItem;
        this.slave = slave;
        this.showAdditionalInfo = showAdditionalInfo;
    }

    protected FeedDetailsBundle(Parcel in) {
        super(in);
        this.feedItem = in.readParcelable(FeedItem.class.getClassLoader());
        this.slave = in.readByte() != 0;
        this.showAdditionalInfo = in.readByte() != 0;
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

    public boolean isShowAdditionalInfo() {
        return showAdditionalInfo;
    }

    public void setShowAdditionalInfo(boolean showAdditionalInfo) {
        this.showAdditionalInfo = showAdditionalInfo;
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
        dest.writeByte(showAdditionalInfo ? (byte) 1 : (byte) 0);
    }
}
