package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BaseFeedModel<T extends IFeedObject> implements Serializable {

    protected User[] users;
    protected BaseFeedModel.Type type;
    protected BaseFeedModel.Action action;
    protected Date postedAt;
    protected T[] entities;
    protected List<Comment> comments;
    @SerializedName("comments_count")
    protected int commentsCount;

    public User[] getUsers() {
        return users;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public Type getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public T[] getEntities() {
        return entities;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String infoText(Resources resources) {
        String action = getActionCaption(resources);
        String type = getTypeCaption(resources);

        return resources.getString(R.string.feed_header, users[0].getFullName(), action, type);
    }

    private String getActionCaption(Resources resources) {
        switch (action) {
            case BOOK:
                return resources.getString(R.string.booked);
            case UPDATE:
                return resources.getString(R.string.updated);
            case ADD:
                return resources.getString(R.string.added);
            case SHARE:
                return resources.getString(R.string.shared);
            case LIKE:
                return resources.getString(R.string.liked);
        }
        return null;
    }


    private String getTypeCaption(Resources resources) {
        switch (type) {
            case TRIP:
                return resources.getString(R.string.feed_trip);
            case PHOTO:
                return resources.getString(R.string.feed_photo);
            case BUCKET_LIST_ITEM:
                return resources.getString(R.string.feed_bucket);
            case POST:
                return "Post";
        }
        return null;
    }

    public enum Type {
        @SerializedName("Trip")
        TRIP(FeedTripEventModel.class),
        @SerializedName("Photo")
        PHOTO(FeedPhotoEventModel.class),
        @SerializedName("BucketListItem")
        BUCKET_LIST_ITEM(FeedBucketEventModel.class),
        AVATAR(FeedAvatarEventModel.class),
        BACKGROUND_PHOTO(FeedCoverEventModel.class),
        POST(FeedPostEventModel.class),

        UNDEFINED(FeedUndefinedEventModel.class);

        private Class clazz;

        Type(Class<? extends BaseFeedModel> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends BaseFeedModel> getClazz() {
            return clazz;
        }
    }

    public enum Action {
        @SerializedName("share")
        SHARE,
        BOOK,
        @SerializedName("add")
        ADD,
        UPDATE,
        @SerializedName("like")
        LIKE
    }
}
