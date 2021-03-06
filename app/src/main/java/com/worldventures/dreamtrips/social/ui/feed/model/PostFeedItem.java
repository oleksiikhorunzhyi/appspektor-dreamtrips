package com.worldventures.dreamtrips.social.ui.feed.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class PostFeedItem extends FeedItem<TextualPost> {

   public PostFeedItem() {
      //do nothing
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
}
