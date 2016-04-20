package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

public class SingleCommentBundle implements Parcelable {
    FeedEntity feedEntity;

    Comment comment;

    public SingleCommentBundle(FeedEntity feedEntity, Comment comment) {
        this.feedEntity = feedEntity;
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }

    public FeedEntity getFeedEntity() {
        return feedEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.feedEntity);
        dest.writeParcelable(this.comment, 0);
    }

    protected SingleCommentBundle(Parcel in) {
        this.feedEntity = (FeedEntity) in.readSerializable();
        this.comment = in.readParcelable(Comment.class.getClassLoader());
    }

    public static final Creator<SingleCommentBundle> CREATOR = new Creator<SingleCommentBundle>() {
        public SingleCommentBundle createFromParcel(Parcel source) {
            return new SingleCommentBundle(source);
        }

        public SingleCommentBundle[] newArray(int size) {
            return new SingleCommentBundle[size];
        }
    };
}
