package com.worldventures.dreamtrips.social.ui.feed.model;

import android.os.Parcelable;

public interface FeedEntityHolder<T extends FeedEntity> extends Parcelable {

   Type getType();

   T getItem();

   enum Type {
      TRIP,
      PHOTO,
      BUCKET_LIST_ITEM,
      POST,
      VIDEO,
      UNDEFINED
   }
}
