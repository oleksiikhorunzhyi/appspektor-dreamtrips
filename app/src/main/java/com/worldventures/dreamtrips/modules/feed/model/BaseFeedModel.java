package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;

import java.io.Serializable;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseFeedModel<T extends IFeedObject> implements Serializable {

    protected BaseFeedModel.Type type;
    protected BaseFeedModel.Action action;

    protected Links links;
    @SerializedName("posted_at")
    protected Date createdAt;
    protected T item;

    public Type getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String infoText(Resources resources) {
        String action = getActionCaption(resources);
        String type = getTypeCaption(resources);

        return resources.getString(R.string.feed_header, links.getUsers().get(0)
                .getFullName(), action, type);
    }

    public Links getLinks() {
        return links;
    }

    public T getItem() {
        return item;
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
        @SerializedName("Post")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFeedModel<?> that = (BaseFeedModel<?>) o;

        return !(item != null ? !item.equals(that.item) : that.item != null);

    }

    @Override
    public int hashCode() {
        return item != null ? item.hashCode() : 0;
    }
}
