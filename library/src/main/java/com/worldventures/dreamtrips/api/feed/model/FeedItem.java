package com.worldventures.dreamtrips.api.feed.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

// don't forget to check FeedItemDeserializer before adding fields here
// Feed and NotificationFeed should be different objects
public interface FeedItem<T extends UniqueIdentifiable> extends EntityHolder<T> {

    @Nullable
    @SerializedName("id") // this field exists only in the notification feed
    Integer id();
    @SerializedName("action")
    Action action();
    @SerializedName("posted_at")
    Date createdAt();
    @Nullable
    @SerializedName("read_at")
    Date readAt(); // this field exists only in the notification feed
    @SerializedName("links")
    FeedItemLinks links();

    enum Action {
        @SerializedName("book")BOOK,
        @SerializedName("add")ADD,
        @SerializedName("update")UPDATE,
        @SerializedName("share")SHARE,
        @SerializedName("like")LIKE,
        @SerializedName("comment")COMMENT,
        @SerializedName("accept_request")ACCEPT_REQUEST,
        @SerializedName("reject_request")REJECT_REQUEST,
        @SerializedName("send_request")SEND_REQUEST,
        @SerializedName("tag_photo")TAG_PHOTO,

        UNKNOWN
    }

}
