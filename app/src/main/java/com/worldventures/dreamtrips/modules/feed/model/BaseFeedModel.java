package com.worldventures.dreamtrips.modules.feed.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Date;

public class BaseFeedModel<T extends IFeedObject> extends BaseEntity {

    protected User[] users;
    protected BaseFeedModel.Type type;
    protected BaseFeedModel.Action action;
    protected Date postedAt;
    protected T[] entities;


    public User[] getUsers() {
        return users;
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

    public String infoText() {
        return users[0].getFullName() + " has posted something";
    }

    public enum Type {
        TRIP(FeedTripEventModel.class),
        PHOTO(FeedPhotoEventModel.class),
        @SerializedName("BucketListItem")
        BUCKET_LIST_ITEM(FeedBucketEventModel.class),
        AVATAR(FeedAvatarEventModel.class),
        BACKGROUND_PHOTO(FeedCoverEventModel.class);

        private Class clazz;

        Type(Class<? extends BaseFeedModel> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends BaseFeedModel> getClazz() {
            return clazz;
        }
    }

    public enum Action {
        SHARE, BOOK, ADD, UPDATE;
    }
}
