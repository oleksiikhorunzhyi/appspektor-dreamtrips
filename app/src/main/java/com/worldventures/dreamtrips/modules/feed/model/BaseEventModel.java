package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseEventModel<T extends IFeedObject> implements Serializable {

    protected BaseEventModel.Type type;
    protected BaseEventModel.Action action;

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
        User user = links.getUsers().get(0);
        String companyName = TextUtils.isEmpty(user.getCompany()) ? "" : " - " + user.getCompany();

        return resources.getString(R.string.feed_header, links.getUsers().get(0)
                .getFullName(), companyName, action, type);
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

        Type(Class<? extends BaseEventModel> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends BaseEventModel> getClazz() {
            return clazz;
        }

        public static Type forClass(Class clazz) {
            return Queryable.from(Type.values()).firstOrDefault(type -> type.getClazz().equals(clazz));
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

        BaseEventModel<?> model = (BaseEventModel<?>) o;

        return action == model.action &&
                !(item != null ? !item.equals(model.item) : model.item != null);
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    public static BaseEventModel create(IFeedObject item, User owner) {
        Type type;
        BaseEventModel baseEventModel;
        if (item instanceof TextualPost) {
            baseEventModel = new FeedPostEventModel();
            type = Type.POST;
        } else if (item instanceof Photo) {
            baseEventModel = new FeedPhotoEventModel();
            type = Type.PHOTO;
        } else if (item instanceof BucketItem) {
            baseEventModel = new FeedBucketEventModel();
            type = Type.BUCKET_LIST_ITEM;
        } else if (item instanceof TripModel) {
            baseEventModel = new FeedTripEventModel();
            type = Type.TRIP;
        } else {
            baseEventModel = new FeedUndefinedEventModel();
            type = Type.UNDEFINED;
        }

        item.setComments(new ArrayList<>());
        baseEventModel.action = Action.ADD;
        baseEventModel.type = type;
        baseEventModel.item = item;
        baseEventModel.createdAt = Calendar.getInstance().getTime();
        baseEventModel.links = Links.forUser(owner);
        return baseEventModel;
    }
}
