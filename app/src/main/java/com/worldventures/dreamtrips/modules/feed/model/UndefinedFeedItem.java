package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class UndefinedFeedItem extends FeedItem<TripModel> {

   public UndefinedFeedItem() {
   }

   public UndefinedFeedItem(Parcel in) {
      super(in);
   }

   public static final Creator<UndefinedFeedItem> CREATOR = new Creator<UndefinedFeedItem>() {
      @Override
      public UndefinedFeedItem createFromParcel(Parcel in) {
         return new UndefinedFeedItem(in);
      }

      @Override
      public UndefinedFeedItem[] newArray(int size) {
         return new UndefinedFeedItem[size];
      }
   };
}
