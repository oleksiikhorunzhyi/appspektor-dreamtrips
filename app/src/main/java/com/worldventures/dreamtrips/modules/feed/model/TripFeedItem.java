package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripFeedItem extends FeedItem<TripModel> {

   public TripFeedItem() {
   }

   public TripFeedItem(Parcel in) {
      super(in);
   }

   @Override
   public String previewImage(Resources res) {
      return getItem().getImageUrl("THUMB");
   }

   public static final Creator<TripFeedItem> CREATOR = new Creator<TripFeedItem>() {
      @Override
      public TripFeedItem createFromParcel(Parcel in) {
         return new TripFeedItem(in);
      }

      @Override
      public TripFeedItem[] newArray(int size) {
         return new TripFeedItem[size];
      }
   };
}
