package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class VideoFeedItem extends FeedItem<Video> {

   public VideoFeedItem() { }

   public VideoFeedItem(Parcel in) {
      super(in);
   }

   public static final Creator<VideoFeedItem> CREATOR = new Creator<VideoFeedItem>() {
      @Override
      public VideoFeedItem createFromParcel(Parcel in) {
         return new VideoFeedItem(in);
      }

      @Override
      public VideoFeedItem[] newArray(int size) {
         return new VideoFeedItem[size];
      }
   };
}
