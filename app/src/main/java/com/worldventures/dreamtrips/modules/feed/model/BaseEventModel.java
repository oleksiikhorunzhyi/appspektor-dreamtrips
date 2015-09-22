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
import com.worldventures.dreamtrips.modules.feed.view.adapter.NotificationHeaderAdapter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseEventModel<T extends IFeedObject> implements Serializable, NotificationHeaderAdapter.HeaderItem {

    protected int id;

    protected BaseEventModel.Type type;
    protected BaseEventModel.Action action;

    protected Links links;
    @SerializedName("posted_at")
    protected Date createdAt;
    protected T item;

    Date read_at;

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
        String result;

        int usersCount = links.getUsers().size();
        if (usersCount == 0) {
            return null;
        }

        User user = links.getUsers().get(0);
        String companyName = TextUtils.isEmpty(user.getCompany()) ? "" : " - " + user.getCompany();

        if (usersCount == 1) {
            result = resources.getString(R.string.feed_header, user.getFullName(), companyName, action, type);
        } else if (usersCount == 2) {
            User user2 = links.getUsers().get(1);
            String companyName2 = TextUtils.isEmpty(user2.getCompany()) ? "" : " - " + user2.getCompany();
            result = resources.getString(R.string.feed_header_two, user.getFullName(), companyName,
                    user2.getFullName(), companyName2, action, type);
        } else {
            result = resources.getString(R.string.feed_header_many, user.getFullName(),
                    companyName, "" + (usersCount - 1), action, type);
        }
        return result;
    }

    public Links getLinks() {
        return links;
    }

    public T getItem() {
        return item;
    }

    public int getId() {
        return id;
    }

    public void setItem(T item) {
        this.item = item;
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
            case ACCEPT_REQUEST:
                return resources.getString(R.string.accept_request);
            case REJECT_REQUEST:
                return resources.getString(R.string.reject_request);
            case COMMENT:
                return resources.getString(R.string.comment);
            case SEND_REQUEST:
                return resources.getString(R.string.send_request);
        }
        return null;
    }


    private String getTypeCaption(Resources resources) {
        if (type == null) return "";

        switch (type) {
            case TRIP:
                return resources.getString(R.string.feed_trip);
            case PHOTO:
                return resources.getString(R.string.feed_photo);
            case BUCKET_LIST_ITEM:
                return resources.getString(R.string.feed_bucket);
            case POST:
                return "Post";
            case UNDEFINED:
            default:
                return "";
        }
    }

    public String previewImage(Resources resources) {
        return "";
    }


    public Date getReadAt() {
        return read_at;
    }

    @Override
    public String getHeaderTitle() {
        return getReadAt() == null ? "New" : "Older";
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
        LIKE,
        @SerializedName("accept_request")
        ACCEPT_REQUEST,
        @SerializedName("reject_request")
        REJECT_REQUEST,
        @SerializedName("comment")
        COMMENT,
        @SerializedName("send_request")
        SEND_REQUEST
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEventModel<?> that = (BaseEventModel<?>) o;

        return !(item != null ? !item.equals(that.item) : that.item != null);
    }

    @Override
    public int hashCode() {
        return item != null ? item.hashCode() : 0;
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
