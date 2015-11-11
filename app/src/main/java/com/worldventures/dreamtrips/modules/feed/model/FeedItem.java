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
public class FeedItem<T extends FeedEntity> extends BaseEntity implements FeedEntityHolder {

    @SerializedName("notification_id")
    protected int notificationId;

    protected FeedItem.Type type = Type.UNDEFINED;
    protected FeedItem.Action action;
    protected T item;
    protected Links links;

    @SerializedName("posted_at")
    protected Date createdAt;
    protected Date readAt;

    public static FeedItem create(FeedEntity item, User owner) {
        Type type;
        FeedItem feedItem;
        if (item instanceof TextualPost) {
            feedItem = new PostFeedItem();
            type = Type.POST;
        } else if (item instanceof Photo) {
            feedItem = new PhotoFeedItem();
            type = Type.PHOTO;
        } else if (item instanceof BucketItem) {
            feedItem = new BucketFeedItem();
            type = Type.BUCKET_LIST_ITEM;
        } else if (item instanceof TripModel) {
            feedItem = new TripFeedItem();
            type = Type.TRIP;
        } else {
            feedItem = new UndefinedFeedItem();
            type = Type.UNDEFINED;
        }

        item.setComments(new ArrayList<>());
        feedItem.action = Action.ADD;
        feedItem.type = type;
        feedItem.item = item;
        feedItem.createdAt = Calendar.getInstance().getTime();
        feedItem.links = Links.forUser(owner);
        return feedItem;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public int getNotificationId() {
        return notificationId;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Action getAction() {
        return action;
    }

    @Override
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

        FeedItem<?> that = (FeedItem<?>) o;

        if (item == null && super.equals(that)) return true;
        return !(item != null ? !item.equals(that.item) : that.item != null);
    }

    @Override
    public int hashCode() {
        if (item == null) return super.hashCode();
        return item != null ? item.hashCode() : 0;
    }

    public FeedItem() {
        super();
    }

    public FeedItem(Parcel in) {
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


    public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    public String detailsText(Resources resources) {
        String type = getTypeCaption(resources);
        String action = resources.getString(R.string.added);

        User user = getItem().getOwner();
        if (user == null) {
            return "";
        }
        String companyName = TextUtils.isEmpty(user.getCompany()) ? "" : " - " + user.getCompany();
        String result = resources.getString(R.string.feed_header, user.getFullName(), companyName, action, type);
        return result;
    }


    public String infoText(Resources resources, int accountId) {
        if (!links.hasUsers()) return null;

        String result;
        User actionOwner = links.getUsers().get(0);
        boolean isAccountsItem = item == null || item.getOwner() == null || accountId == item.getOwner().getId();
        boolean ownAction = isAccountsItem && accountId == actionOwner.getId();
        String action = getActionCaption(resources, isAccountsItem, ownAction);
        String type = getTypeCaption(resources);
        String companyName = TextUtils.isEmpty(actionOwner.getCompany()) ? "" : " - " + actionOwner.getCompany();

        int usersCount = links.getUsers().size();
        if (usersCount == 1) {
            result = resources.getString(R.string.feed_header, actionOwner.getFullName(), companyName, action, type);
        } else if (usersCount == 2) {
            User user2 = links.getUsers().get(1);
            String companyName2 = TextUtils.isEmpty(user2.getCompany()) ? "" : " - " + user2.getCompany();
            result = resources.getString(R.string.feed_header_two, actionOwner.getFullName(), companyName,
                    user2.getFullName(), companyName2, action, type);
        } else {
            result = resources.getString(R.string.feed_header_many, actionOwner.getFullName(),
                    companyName, "" + (usersCount - 1), action, type);
        }
        return result;
    }

    private String getActionCaption(Resources resources, boolean isAccountsItem, boolean ownAction) {
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
                return isAccountsItem && !ownAction ? resources.getString(R.string.liked) : resources.getString(R.string.liked_foreign);
            case ACCEPT_REQUEST:
                return resources.getString(R.string.accept_request);
            case REJECT_REQUEST:
                return resources.getString(R.string.reject_request);
            case COMMENT:
                return isAccountsItem && !ownAction ? resources.getString(R.string.comment) : resources.getString(R.string.comment_foreign);
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
