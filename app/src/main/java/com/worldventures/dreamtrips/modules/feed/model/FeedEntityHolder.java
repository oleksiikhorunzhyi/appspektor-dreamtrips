package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public interface FeedEntityHolder<T extends FeedEntity> extends Parcelable {

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
