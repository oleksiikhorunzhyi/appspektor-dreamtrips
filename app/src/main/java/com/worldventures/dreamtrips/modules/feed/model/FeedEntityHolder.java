package com.worldventures.dreamtrips.modules.feed.model;

import com.google.gson.annotations.SerializedName;

public interface FeedEntityHolder<T extends FeedEntity> {

    Type getType();

    T getItem();

    enum Type {
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
}
