package com.worldventures.dreamtrips.api.entity.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

public interface BaseEntityHolder<T> {

    @SerializedName("type")
    Type type();

    @Nullable
    @SerializedName("item")
    T entity();

    enum Type {
        @SerializedName("Trip")
        TRIP,
        @SerializedName("Post")
        POST,
        @SerializedName("Photo")
        PHOTO,
        @SerializedName("BucketListItem")
        BUCKET_LIST_ITEM,

        UNKNOWN
    }

}
