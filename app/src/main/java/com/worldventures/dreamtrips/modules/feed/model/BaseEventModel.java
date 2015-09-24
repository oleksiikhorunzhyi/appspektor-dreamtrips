package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseEventModel<T extends IFeedObject> extends BaseEntity {

    @SerializedName("notification_id")
    protected int notificationId;

    protected BaseEventModel.Type type = Type.UNDEFINED;
    protected BaseEventModel.Action action;
    protected T item;
    protected Links links;

    @SerializedName("posted_at")
    protected Date createdAt;
    protected Date readAt;

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

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public int getNotificationId() {
        return notificationId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Action getAction() {
        return action;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Links getLinks() {
        return links;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Equals / Hashcode
    ///////////////////////////////////////////////////////////////////////////

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

    public BaseEventModel() {
    }

    public BaseEventModel(Parcel in) {
        super(in);
        this.notificationId = in.readInt();
        this.type = (Type) in.readSerializable();
        this.action = (Action) in.readSerializable();
        this.item = (T) in.readSerializable();
        this.links = (Links) in.readSerializable();
        this.createdAt = (Date) in.readSerializable();
        this.readAt = (Date) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(notificationId);
        dest.writeSerializable(type);
        dest.writeSerializable(action);
        dest.writeSerializable(item);
        dest.writeSerializable(links);
        dest.writeSerializable(createdAt);
        dest.writeSerializable(readAt);
    }


    public static final Creator<BaseEventModel> CREATOR = new Creator<BaseEventModel>() {
        @Override
        public BaseEventModel createFromParcel(Parcel in) {
            return new BaseEventModel(in);
        }

        @Override
        public BaseEventModel[] newArray(int size) {
            return new BaseEventModel[size];
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////
    // Inner
    ///////////////////////////////////////////////////////////////////////////

    public enum Type {
        @SerializedName("Trip")
        TRIP,
        @SerializedName("Photo")
        PHOTO,
        @SerializedName("BucketListItem")
        BUCKET_LIST_ITEM,
        @SerializedName("Post")
        POST,
        UNDEFINED
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
        @SerializedName("comment")
        COMMENT,
        @SerializedName("accept_request")
        ACCEPT_REQUEST,
        @SerializedName("reject_request")
        REJECT_REQUEST,
        @SerializedName("send_request")
        SEND_REQUEST,
    }

}
