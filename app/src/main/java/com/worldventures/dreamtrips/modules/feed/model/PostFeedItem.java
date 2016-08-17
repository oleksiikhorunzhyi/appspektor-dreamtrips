package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;

public class PostFeedItem extends FeedItem<TextualPost> {

   public PostFeedItem() {
   }

   public PostFeedItem(Parcel in) {
      super(in);
   }

   public static final Creator<PostFeedItem> CREATOR = new Creator<PostFeedItem>() {
      @Override
      public PostFeedItem createFromParcel(Parcel in) {
         return new PostFeedItem(in);
      }

      @Override
      public PostFeedItem[] newArray(int size) {
         return new PostFeedItem[size];
      }
   };

   @Override
   public String getOriginalText() {
      return getItem().getDescription();
   }
}
