package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketFeedItem extends FeedItem<BucketItem> {

   @Override
   public String previewImage(Resources res) {
      int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
      int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
      return getItem().getCoverUrl(width, height);
   }

   public BucketFeedItem() {
   }

   public BucketFeedItem(Parcel in) {
      super(in);
   }

   public static final Creator<BucketFeedItem> CREATOR = new Creator<BucketFeedItem>() {
      @Override
      public BucketFeedItem createFromParcel(Parcel in) {
         return new BucketFeedItem(in);
      }

      @Override
      public BucketFeedItem[] newArray(int size) {
         return new BucketFeedItem[size];
      }
   };
}
