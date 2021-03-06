package com.worldventures.dreamtrips.social.ui.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class VideoFeedItem extends FeedItem<Video> {

   public VideoFeedItem() {
      //do nothing
   }

   public VideoFeedItem(Parcel in) {
      super(in);
   }

   @Override
   public String previewImage(Resources res) {
      return getItem().getThumbnail();
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
