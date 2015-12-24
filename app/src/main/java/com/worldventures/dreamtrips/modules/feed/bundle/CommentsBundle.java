package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class CommentsBundle implements Parcelable {

    protected FeedEntity feedEntity;
    protected boolean openKeyboard;
    protected boolean showLikersPanel;

    public CommentsBundle(FeedEntity feedEntity) {
        this(feedEntity, false);
    }

    public CommentsBundle(FeedEntity feedEntity, boolean openKeyboard) {
        this(feedEntity, openKeyboard, false);
    }

    public CommentsBundle(FeedEntity feedEntity, boolean openKeyboard, boolean showLikers) {
        this.feedEntity = feedEntity;
        this.openKeyboard = openKeyboard;
        this.showLikersPanel = showLikers;
    }

    public FeedEntity getFeedEntity() {
        return feedEntity;
    }

    public boolean isOpenKeyboard() {
        return openKeyboard;
    }

    public void setOpenKeyboard(boolean openKeyboard) {
        this.openKeyboard = openKeyboard;
    }

    public boolean showLikersPanel() {
        return showLikersPanel;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.feedEntity);
        dest.writeByte(openKeyboard ? (byte) 1 : (byte) 0);
        dest.writeByte(showLikersPanel ? (byte) 1 : (byte) 0);
    }

    protected CommentsBundle(Parcel in) {
        this.feedEntity = (FeedEntity) in.readSerializable();
        this.openKeyboard = in.readByte() != 0;
        this.showLikersPanel = in.readByte() != 0;
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
